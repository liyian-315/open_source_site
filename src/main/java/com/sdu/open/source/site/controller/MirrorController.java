package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.Document;
import com.sdu.open.source.site.entity.Menu;
import com.sdu.open.source.site.entity.Mirror;
import com.sdu.open.source.site.service.MirrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-08-26  09:36
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/public")
@Slf4j
public class MirrorController {
    private MirrorService mirrorService;

    @Autowired
    private void setMirrorService(MirrorService mirrorService) {
        this.mirrorService = mirrorService;
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
    private List<Menu> getMirrorsMenu() {
        return mirrorService.getMirrorsMenu();
    }
}
