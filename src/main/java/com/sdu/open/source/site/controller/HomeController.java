package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.ApiResponse;
import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.entity.HomeCarousel;
import com.sdu.open.source.site.enums.CopyWritingAreas;
import com.sdu.open.source.site.service.CopyWritingService;
import com.sdu.open.source.site.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@Slf4j
public class HomeController {

    private HomeService homeService;
    private CopyWritingService copyWritingService;

    @Autowired
    private void setHomeService(HomeService homeService) {
        this.homeService = homeService;
    }

    @Autowired
    private void setCopyWritingService(CopyWritingService copyWritingService) {
        this.copyWritingService = copyWritingService;
    }

    @GetMapping("/home_carousel")
    public ResponseEntity<List<HomeCarousel>> getHomeCarousel() {
        try {
            List<HomeCarousel> carousels = homeService.getHomeCarousels();

            if (carousels == null || carousels.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(carousels, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/home_projects")
    public ResponseEntity<List<CopyWriting>> getHomeProjects() {
        try {
            List<CopyWriting> projects = copyWritingService.getCwListByArea(CopyWritingAreas.HOME_PROJECT_DISPLAY.getCode());

            if (projects == null || projects.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取首页项目列表失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 管理员：添加首页项目展示
     */
    @PostMapping("/admin/home_projects")
    public ResponseEntity<?> addHomeProject(@RequestBody CopyWriting copyWriting) {
        try {
            if (copyWriting == null || copyWriting.getTitle() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "项目标题不能为空"));
            }

            boolean success = copyWritingService.addHomeProject(copyWriting);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("添加成功"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "首页项目展示已达上限（最多4个）"));
            }
        } catch (Exception e) {
            log.error("添加首页项目失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * 管理员：删除首页项目展示
     */
    @DeleteMapping("/admin/home_projects/{id}")
    public ResponseEntity<?> deleteHomeProject(@PathVariable Long id) {
        try {
            boolean success = copyWritingService.deleteHomeProject(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("删除成功"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "删除失败"));
            }
        } catch (Exception e) {
            log.error("删除首页项目失败，id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * 管理员：更新首页项目展示
     */
    @PutMapping("/admin/home_projects")
    public ResponseEntity<?> updateHomeProject(@RequestBody CopyWriting copyWriting) {
        try {
            if (copyWriting == null || copyWriting.getId() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "项目ID不能为空"));
            }

            boolean success = copyWritingService.updateHomeProject(copyWriting);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("更新成功"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "更新失败"));
            }
        } catch (Exception e) {
            log.error("更新首页项目失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * 获取首页活动列表
     */
    @GetMapping("/home_events")
    public ResponseEntity<List<CopyWriting>> getHomeEvents() {
        try {
            List<CopyWriting> events = copyWritingService.getCwListByArea(CopyWritingAreas.HOME_EVENT_DISPLAY.getCode());

            if (events == null || events.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取首页活动列表失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 管理员：添加首页活动展示
     */
    @PostMapping("/admin/home_events")
    public ResponseEntity<?> addHomeEvent(@RequestBody CopyWriting copyWriting) {
        try {
            if (copyWriting == null || copyWriting.getTitle() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "活动标题不能为空"));
            }

            boolean success = copyWritingService.addHomeEvent(copyWriting);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("添加成功"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "首页活动展示已达上限（最多4个）"));
            }
        } catch (Exception e) {
            log.error("添加首页活动失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * 管理员：删除首页活动展示
     */
    @DeleteMapping("/admin/home_events/{id}")
    public ResponseEntity<?> deleteHomeEvent(@PathVariable Long id) {
        try {
            boolean success = copyWritingService.deleteHomeEvent(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("删除成功"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "删除失败"));
            }
        } catch (Exception e) {
            log.error("删除首页活动失败，id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * 管理员：更新首页活动展示
     */
    @PutMapping("/admin/home_events")
    public ResponseEntity<?> updateHomeEvent(@RequestBody CopyWriting copyWriting) {
        try {
            if (copyWriting == null || copyWriting.getId() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "活动ID不能为空"));
            }

            boolean success = copyWritingService.updateHomeEvent(copyWriting);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("更新成功"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "更新失败"));
            }
        } catch (Exception e) {
            log.error("更新首页活动失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }
}
