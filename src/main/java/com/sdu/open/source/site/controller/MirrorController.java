package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.Menu;
import com.sdu.open.source.site.entity.Mirror;
import com.sdu.open.source.site.service.MirrorService;
import com.sdu.open.source.site.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-08-26  09:36
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class MirrorController {
    private MirrorService mirrorService;
    private MinioService minioService;

    @Autowired
    private void setMirrorService(MirrorService mirrorService) {
        this.mirrorService = mirrorService;
    }

    @Autowired
    private void setMinioService(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/mirrors")
    private ResponseEntity<?> getMirrors(@RequestBody Mirror mirror) throws Exception {
        try {
            List<Mirror> mirrors = mirrorService.getMirrors(mirror);
            if (mirrors == null || mirrors.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(mirrors, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/mirrors_menu")
    private ResponseEntity<?> getMirrorsMenu() {
        List<Menu> mirrorsMenu = mirrorService.getMirrorsMenu();
        if (mirrorsMenu == null || mirrorsMenu.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(mirrorsMenu, HttpStatus.OK);
    }

    // ==================== 管理员接口：设备管理 ====================

    /**
     * 获取指定架构下的所有设备
     */
    @GetMapping("/admin/devices/{arch}")
    private ResponseEntity<?> getDevicesByArch(@PathVariable String arch) {
        try {
            List<Menu> devices = mirrorService.getDevicesByArch(arch);
            return new ResponseEntity<>(devices, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取设备列表失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 添加设备
     */
    @PostMapping("/admin/device")
    private ResponseEntity<Map<String, String>> addDevice(@RequestBody Menu menu) {
        Map<String, String> response = new HashMap<>();
        try {
            String result = mirrorService.addDevice(menu);
            if ("success".equals(result)) {
                response.put("message", "添加成功");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", result);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("添加设备失败", e);
            response.put("message", "添加失败：" + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新设备
     */
    @PutMapping("/admin/device")
    private ResponseEntity<Map<String, String>> updateDevice(@RequestBody Menu menu) {
        Map<String, String> response = new HashMap<>();
        try {
            String result = mirrorService.updateDevice(menu);
            if ("success".equals(result)) {
                response.put("message", "更新成功");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", result);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("更新设备失败", e);
            response.put("message", "更新失败：" + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/admin/device/{id}/{arch}")
    private ResponseEntity<Map<String, String>> deleteDevice(@PathVariable Long id, @PathVariable String arch) {
        Map<String, String> response = new HashMap<>();
        try {
            String result = mirrorService.deleteDevice(id, arch);
            if ("success".equals(result)) {
                response.put("message", "删除成功");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", result);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("删除设备失败", e);
            response.put("message", "删除失败：" + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==================== 管理员接口：镜像管理 ====================

    /**
     * 添加镜像
     */
    @PostMapping("/admin/mirror")
    private ResponseEntity<Map<String, String>> addMirror(@RequestBody Mirror mirror) {
        Map<String, String> response = new HashMap<>();
        try {
            String result = mirrorService.addMirror(mirror);
            if ("success".equals(result)) {
                response.put("message", "添加成功");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", result);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("添加镜像失败", e);
            response.put("message", "添加失败：" + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新镜像
     */
    @PutMapping("/admin/mirror")
    private ResponseEntity<Map<String, String>> updateMirror(@RequestBody Mirror mirror) {
        Map<String, String> response = new HashMap<>();
        try {
            String result = mirrorService.updateMirror(mirror);
            if ("success".equals(result)) {
                response.put("message", "更新成功");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", result);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("更新镜像失败", e);
            response.put("message", "更新失败：" + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除镜像
     */
    @DeleteMapping("/admin/mirror/{id}")
    private ResponseEntity<Map<String, String>> deleteMirror(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            String result = mirrorService.deleteMirror(id);
            if ("success".equals(result)) {
                response.put("message", "删除成功");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", result);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("删除镜像失败", e);
            response.put("message", "删除失败：" + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 上传镜像文件到 MinIO
     */
    @PostMapping("/admin/mirror/upload")
    private ResponseEntity<Map<String, String>> uploadMirrorFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "mirrors/") String folder) {
        Map<String, String> response = new HashMap<>();
        try {
            if (file == null || file.isEmpty()) {
                response.put("message", "文件不能为空");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            String fileUrl = minioService.uploadMirrorFile(file, folder);

            response.put("message", "上传成功");
            response.put("url", fileUrl);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("上传镜像文件失败", e);
            response.put("message", "上传失败：" + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
