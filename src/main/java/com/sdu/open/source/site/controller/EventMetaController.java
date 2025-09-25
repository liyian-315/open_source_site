// controller/EventMetaController.java
package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.EventMetaDTO;
import com.sdu.open.source.site.service.EventMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sdu.open.source.site.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
public class EventMetaController {
    private final EventMetaService metaService;

    @GetMapping("/api/events/meta")
    public ApiResponse<EventMetaDTO> meta() {
        return ApiResponse.success(metaService.load());
    }
}
