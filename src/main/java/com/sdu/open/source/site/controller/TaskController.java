package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.ApiResponse;
import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.Task;
import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.enums.TaskStatus;
import com.sdu.open.source.site.service.TaskService;
import com.sdu.open.source.site.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@Slf4j
public class TaskController {

    private TaskService taskService;
    private UserService userService;

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //返回当前用户已领取但未完成的任务数量
    @GetMapping("/fetchReceivedTaskCount")
    public ResponseEntity<?> fetchReceivedTaskCount(@RequestParam("params") String collectionUser) {
        try {
            int count = taskService.getUserTaskCount(collectionUser, TaskStatus.COMPLETED.getCode());
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            log.error("用户任务统计失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/fetchMyTasks")
    public ResponseEntity<List<Task>> fetchMyTasks(@RequestParam("params") String collectionUser) {
        try {
            List<Task> tasks = taskService.getTasksByCollectionUser(collectionUser);
            if (tasks == null || tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            for (Task task : tasks) {
                task.setTaskClassName(taskService.getClassNameById(task.getTaskClass()).getName());
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            log.error("我的任务查询失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/fetchTasksByCategory")
    public ResponseEntity<List<Task>> fetchTasksByCategory(@RequestParam("params") Long categoryId) {
        try {
            List<Task> tasks = taskService.getTasksByCategoryId(categoryId);
            if (tasks == null || tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            log.error("分类下任务查询失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetchTaskCategories")
    public ResponseEntity<List<TaskClass>> fetchTaskCategories() {
        try {
            List<TaskClass> taskClassList = taskService.getTaskClassList();
            if (taskClassList == null || taskClassList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(taskClassList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("任务分类查询失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 领取任务
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PutMapping("/claimTask")
    public ResponseEntity<?> claimTask(@RequestBody RequestParamDTO param) throws Exception {
        try {
            // 获取请求参数
            String username = param.getUsername();
            Long taskId = param.getTaskId();
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(400, "用户名为空"));
            }
            if (taskId == null) {
                return ResponseEntity.ok(ApiResponse.error(400, "任务 ID 为空"));
            }
            User user = userService.findByUsername(username);
            if (user == null) {
                log.warn("任务领取失败，用户不存在: {}", username);
                return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
            }
            if (!user.getHasSignedPdf()) {
                return ResponseEntity.ok(ApiResponse.error(400, "用户未签署协议"));
            }

            // 验证任务是否存在
            Task task = taskService.findById(taskId);
            if (task == null) {
                log.warn("任务领取失败，任务不存在: {}", taskId);
                return ResponseEntity.ok(ApiResponse.error(404, "任务不存在"));
            }

            // 验证任务状态是否可领取
            if (!TaskStatus.TO_BE_RECEIVED.getCode().equals(task.getTaskStatus())) {
                log.warn("任务 {} 不可领取，当前状态: {}", taskId, task.getTaskStatus());
                return ResponseEntity.ok(ApiResponse.error(400, "任务不可领取，当前状态:"));
            }

            // 验证用户是否已领取该任务
            if (username.equals(task.getCollectionUser())) {
                return ResponseEntity.ok(ApiResponse.error(400, "您已领取该任务"));
            }

            // 验证用户领取任务数量是否超限
            int userTaskCount = taskService.getUserTaskCount(username, TaskStatus.COMPLETED.getCode());
            if (userTaskCount >= 2) {
                return ResponseEntity.ok(ApiResponse.error(400, "最多只能领取 2 个任务"));
            }

            taskService.updateTask(param);

            log.info("用户 {} 成功领取任务 {}", username, taskId);
            return ResponseEntity.ok(ApiResponse.success(null, "任务领取成功"));

        } catch (Exception e) {
            log.error("任务领取异常", e);
            return ResponseEntity.ok(ApiResponse.error(500, "任务领取失败，请稍后重试"));
        }
    }
}
