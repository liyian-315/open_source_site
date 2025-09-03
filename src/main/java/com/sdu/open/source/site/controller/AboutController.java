package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.service.AboutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-09-01  17:05
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class AboutController {

    private AboutService aboutService;

    @Autowired
    private void setAboutService(AboutService aboutService) {
        this.aboutService = aboutService;
    }

    @GetMapping("/about_text")
    public ResponseEntity<String> getAboutText(CopyWriting cw) {
        try {
            if (cw == null || cw.getArea() == null || cw.getArea().trim().isEmpty()) {
                return new ResponseEntity<>("区域参数不能为空", HttpStatus.BAD_REQUEST);
            }
            String aboutText = aboutService.getAboutText(cw.getArea().trim());
            if (aboutText == null || aboutText.trim().isEmpty()) {
                return new ResponseEntity<>("未找到对应区域的文本内容", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(aboutText, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("获取文本内容失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
