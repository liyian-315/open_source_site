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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Getter
    @Value("${minio.bucket}")
    private String bucket;

    /**
     * 对外可访问的公共前缀，例如：
     *   https://riscv-cn.org/sduproxy
     * 为空时，退回使用预签名 URL。
     */
    @Value("${minio.public-url:}")
    private String publicUrl;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void ensureBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created bucket: {}", bucket);
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
}
