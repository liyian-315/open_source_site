package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
//
//    @GetMapping("/fetchReceivedTaskCount")
//    public ResponseEntity<?> fetchReceivedTaskCount() {
//    }
//
//    @GetMapping("/fetchMyTasks")
//    public ResponseEntity<?> fetchMyTasks() {
//    }
//
//    @GetMapping("/fetchTasksByCategory")
//    public ResponseEntity<?> fetchTasksByCategory() {
//    }

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
