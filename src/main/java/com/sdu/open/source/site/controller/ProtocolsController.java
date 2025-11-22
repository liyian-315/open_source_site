package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.DsProtocol;
import com.sdu.open.source.site.service.DsProtocolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProtocolsController {
    private final DsProtocolService dsProtocolService;

    public ProtocolsController(DsProtocolService dsProtocolService) {
        this.dsProtocolService = dsProtocolService;
    }

    @GetMapping("/protocols")
    public ResponseEntity<?> getProtocols() {
        try {
            List<DsProtocol> dsProtocols = dsProtocolService.findAll();
            return ResponseEntity.ok(dsProtocols);
        } catch (Exception e) {
            log.error("getProtocols error", e);
            return ResponseEntity.internalServerError().body("getProtocols error");
        }
    }
}
