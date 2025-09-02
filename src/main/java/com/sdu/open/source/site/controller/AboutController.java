package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.service.AboutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String getAboutText(CopyWriting cw) {
        return aboutService.getAboutText(cw.getArea());
    }

}
