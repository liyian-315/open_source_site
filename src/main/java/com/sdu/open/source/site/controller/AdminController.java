package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.service.TaskService;
import com.sdu.open.source.site.service.TaskUserService;
import com.sdu.open.source.site.service.UserService;
import com.sdu.open.source.site.vo.AdminTaskVO;
import com.sdu.open.source.site.vo.PageResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class AdminController {

    private final UserService userService;
    private final TaskService taskService;
    private final TaskUserService taskUserService;

    public AdminController(UserService userService, TaskService taskService, TaskUserService taskUserService) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskUserService = taskUserService;
    }

    @PostMapping("/admin/update_user")
    public ResponseEntity<?> updateUser(@RequestBody RequestParamDTO param) {
        try {
            userService.updateUserByParam(param);
            return new ResponseEntity<>("更新用户成功", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("更新用户失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/select_user")
    public ResponseEntity<?> selectUser(@RequestBody RequestParamDTO param) {
        try {
            PageResultVO<User> pageResult = userService.selectByUsernameOrFullname(param);
            return new ResponseEntity<>(pageResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("用户查询失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/delete_user")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        if (id == null) {
            return new ResponseEntity<>("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }
        try {
            boolean deleteSuccess = userService.deleteUserById(id);
            if (deleteSuccess) {
                log.info("用户删除成功：用户ID={}", id);
                return new ResponseEntity<>("用户删除成功", HttpStatus.OK); // 200成功
            } else {
                log.warn("用户删除失败：未找到该用户，用户ID={}", id);
                return new ResponseEntity<>("删除失败：未找到ID为" + id + "的用户", HttpStatus.NOT_FOUND); // 404用户不存在
            }
        } catch (Exception e) {
            return new ResponseEntity<>("删除用户失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/select_task")
    public ResponseEntity<?> selectTask(@RequestBody RequestParamDTO param) {
        try {
            if (param == null) {
                param = new RequestParamDTO();
            }
            PageResultVO<AdminTaskVO> pageResult = taskService.selectByParam(param);
            return new ResponseEntity<>(pageResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("任务查询失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/update_task_status")
    public ResponseEntity<?> updateTaskStatus(@RequestBody RequestParamDTO param) {
        try {
            if (param == null || param.getTaskUserId() == null || (param.getTaskStatus() == null && param.getRecogStatus() == null)) {
                return new ResponseEntity<>("参数不能为空提示", HttpStatus.BAD_REQUEST);
            }
            boolean success = taskUserService.update(param);
            if (success) {
                return ResponseEntity.ok("任务状态更新成功");
            } else {
                return ResponseEntity.status(400).body("任务状态更新失败，任务不存在或状态不合法");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("任务状态更新失败: " + e.getMessage());
        }
    }
    @PostMapping("/admin/create_task")
    public ResponseEntity<?> createTask(@RequestBody RequestParamDTO param) {
        try {
            if (param == null || param.getTaskName() == null ) {
                return new ResponseEntity<>("参数不能为空提示", HttpStatus.BAD_REQUEST);
            }
            boolean success = taskService.createTaskWithClass(param);
            if (success) {
                return ResponseEntity.ok("任务创建成功");
            } else {
                return ResponseEntity.status(400).body("任务创建失败，任务类型不合法");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("任务创建失败: " + e.getMessage());
        }
    }
}
