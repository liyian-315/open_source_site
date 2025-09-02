package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.Menu;
import com.sdu.open.source.site.entity.Mirror;
import com.sdu.open.source.site.service.MirrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Autowired
    private void setMirrorService(MirrorService mirrorService) {
        this.mirrorService = mirrorService;
    }

    @GetMapping("/mirrors")
    private List<Mirror> getMirrors() {
        return mirrorService.getMirrors();
    }

    @GetMapping("/mirrors_menu")
    private List<Menu> getMirrorsMenu() {
        return mirrorService.getMirrorsMenu();
    }
}
