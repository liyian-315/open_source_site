package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.service.TaskService;
import com.sdu.open.source.site.service.TaskUserService;
import com.sdu.open.source.site.service.UserService;
import com.sdu.open.source.site.vo.AdminTaskVO;
import com.sdu.open.source.site.vo.PageResultVO;
import com.sdu.open.source.site.vo.TaskEditVO;
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

    @PostMapping("/admin/task_class")
    public ResponseEntity<?> createTaskClass(@RequestBody TaskClass taskClass) {
        try {
            TaskClass createdTaskClass = taskService.createTaskClass(taskClass);
            return ResponseEntity.ok(createdTaskClass);
        } catch (Exception e) {
            log.error("createTaskClass error", e);
            return ResponseEntity.internalServerError().body("createTaskClass error");
        }
    }

    /**
     * 查询任务编辑视图数据
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/admin/tasks_edit")
    public ResponseEntity<?> getTasksForEdit(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        try {
            PageResultVO<TaskEditVO> pageResult = taskService.selectTaskEditVO(pageNum, pageSize);
            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            log.error("查询任务编辑数据失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("查询任务编辑数据失败");
        }
    }

    /**
     * 更新任务信息
     * @param taskId 任务ID
     * @param param 请求参数
     * @return 更新结果
     */
    @PutMapping("/admin/task/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long taskId,
            @RequestBody RequestParamDTO param
    ) {
        try {
            if (taskId == null) {
                return ResponseEntity.badRequest().body("任务ID不能为空");
            }
            param.setTaskId(taskId);
            boolean success = taskService.updateTaskInfo(param);
            if (success) {
                return ResponseEntity.ok("任务更新成功");
            } else {
                return ResponseEntity.status(400).body("任务更新失败");
            }
        } catch (Exception e) {
            log.error("任务更新失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("任务更新失败: " + e.getMessage());
        }
    }
}
