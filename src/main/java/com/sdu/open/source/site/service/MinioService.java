package com.sdu.open.source.site.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Getter
    @Value("${minio.bucket}")
    private String bucket;
    @Getter
    @Value("${minio.mirror-bucket}")
    private String mirrorBucket;

    /**
     * 对外可访问的公共前缀，例如：
     *   https://riscv-cn.org/sduproxy
     * 为空时，退回使用预签名 URL。
     */
    @Value("${minio.public-url:}")
    private String publicUrl;

    /**
     * 镜像文件上传的目录前缀
     */
    @Value("${minio.mirror-folder:mirrors/}")
    private String mirrorFolder;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void ensureBucket() {
        try {
            // 确保默认 bucket 存在
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created bucket: {}", bucket);
            }

            // 确保 mirrorBucket 存在
            boolean mirrorExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(mirrorBucket).build());
            if (!mirrorExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(mirrorBucket).build());
                log.info("Created mirror bucket: {}", mirrorBucket);
            }
        } catch (Exception e) {
            log.error("Ensure bucket failed", e);
        }
    }

    public String putObject(String objectName, MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)) contentType = "application/octet-stream";

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );
        return objectName;
    }

    /** 返回公开 URL（配置了 public-url 时）或预签名 URL（否则）。 */
    public String getFileUrl(String objectName) throws Exception {
        if (StringUtils.hasText(publicUrl)) {
            // public-url 可能带路径前缀（如 /sduproxy），这里用安全拼接并对 objectName 做路径段编码
            String base = trimTrailingSlash(publicUrl);
            String path = encodePath(objectName);
            return joinUrl(joinUrl(base, bucket), path);
        }
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .method(Method.GET)
                        .build()
        );
    }

    /** 返回镜像文件的公开 URL（使用 mirrorBucket） */
    public String getMirrorFileUrl(String objectName) throws Exception {
        if (StringUtils.hasText(publicUrl)) {
            // public-url 可能带路径前缀（如 /sduproxy），这里用安全拼接并对 objectName 做路径段编码
            String base = trimTrailingSlash(publicUrl);
            String path = encodePath(objectName);
            return joinUrl(joinUrl(base, mirrorBucket), path);
        }
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(mirrorBucket)
                        .object(objectName)
                        .method(Method.GET)
                        .build()
        );
    }

    /** 去掉末尾 /（不动开头，以支持 https://host/prefix 这种形式） */
    private static String trimTrailingSlash(String s) {
        if (s == null) return "";
        while (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return s;
    }

    /** 安全拼接 URL，去重中间斜杠 */
    private static String joinUrl(String left, String right) {
        if (!StringUtils.hasText(left)) return right == null ? "" : right;
        if (!StringUtils.hasText(right)) return left;
        String l = left.endsWith("/") ? left.substring(0, left.length() - 1) : left;
        String r = right.startsWith("/") ? right.substring(1) : right;
        return l + "/" + r;
    }

    /** 对每个路径段做 URL 编码，保留斜杠分隔 */
    private static String encodePath(String path) {
        if (path == null) return "";
        return Arrays.stream(path.split("/"))
                .map(seg -> URLEncoder.encode(seg, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.joining("/"));
    }

    /**
     * 上传镜像文件到 MinIO
     *
     * @param file 上传的文件
     * @param folder 上传目录，如 "mirrors/" 或 "images/riscv/"
     * @return MinIO 文件访问 URL
     * @throws Exception 上传异常
     */
    public String uploadMirrorFile(MultipartFile file, String folder) throws Exception {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 确保文件夹路径以 / 结尾
        if (folder != null && !folder.isEmpty() && !folder.endsWith("/")) {
            folder = folder + "/";
        }
        // 如果 folder 为空，使用默认的 mirrorFolder 配置
        if (folder == null || folder.isEmpty()) {
            folder = mirrorFolder;
        }

        // 使用原始文件名
        String objectName = folder + originalFilename;

        try (InputStream inputStream = file.getInputStream()) {
            // 获取文件类型
            String contentType = file.getContentType();
            if (!StringUtils.hasText(contentType)) {
                contentType = "application/octet-stream";
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(mirrorBucket)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

            // 获取文件 URL（使用 mirrorBucket）
            String fileUrl = getMirrorFileUrl(objectName);

            log.info("镜像文件上传成功：原始文件名={}, MinIO对象名={}, URL={}", originalFilename, objectName, fileUrl);

            return fileUrl;

        } catch (Exception e) {
            log.error("镜像文件上传失败：{}", e.getMessage(), e);
            throw new Exception("文件上传失败：" + e.getMessage(), e);
        }
    }

    /**
     * 删除 MinIO 上的文件
     *
     * @param objectName 对象名称
     */
    public void deleteFile(String objectName) {
        try {
            if (objectName == null || objectName.isEmpty()) {
                return;
            }

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );

            log.info("MinIO文件删除成功：objectName={}", objectName);
        } catch (Exception e) {
            log.error("删除MinIO文件失败：{}", e.getMessage(), e);
        }
    }
}
