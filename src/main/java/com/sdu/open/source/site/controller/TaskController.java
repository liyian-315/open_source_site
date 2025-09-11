package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.Task;
import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.enums.TaskStatus;
import com.sdu.open.source.site.service.TaskService;
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

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

//    @GetMapping("/claimTask")
//    public ResponseEntity<?> claimTask() {
//    }

     //返回当前用户已领取但未完成的任务数量
    @GetMapping("/fetchReceivedTaskCount")
    public ResponseEntity<?> fetchReceivedTaskCount(@RequestParam("params") String collectionUser) {
        try {
            int count = taskService.getUserTaskCount(collectionUser, TaskStatus.COMPLETED.getCode());
            return new ResponseEntity<>(count, HttpStatus.OK);
        }catch (Exception e) {
            log.error("用户任务统计失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
//
    @GetMapping("/fetchMyTasks")
    public ResponseEntity<List<Task>> fetchMyTasks(@RequestParam("params") String collectionUser) {
        try {
            List<Task> tasks = taskService.getTasksByCollectionUser(collectionUser);
            if (tasks == null || tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
}
