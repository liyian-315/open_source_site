package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.ApiResponse;
import com.sdu.open.source.site.entity.Event;
import com.sdu.open.source.site.service.EventService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventController {
    @Resource private EventService eventService;

    @GetMapping("/events")
    public ApiResponse<Map<String,Object>> list(@RequestParam(required = false) String q,
                                                @RequestParam(required = false) String type,
                                                @RequestParam(required = false) String tag,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(eventService.list(q, type, tag, page, pageSize));
    }

    @GetMapping("/events/{slug}")
    public ApiResponse<Map<String,Object>> detail(@PathVariable String slug) {
        return ApiResponse.success(eventService.detail(slug));
    }

    // 先保留一个后台接口
    @PostMapping("/admin/events")
    public ApiResponse<Long> create(@RequestBody Event e) {
        return ApiResponse.success(eventService.create(e));
    }
    @PutMapping("/admin/events/{id}")
    public ApiResponse<Integer> update(@PathVariable Long id, @RequestBody Event e) {
        e.setId(id);
        return ApiResponse.success(eventService.update(e));
    }
}
