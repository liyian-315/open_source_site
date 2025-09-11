package com.sdu.open.source.site.service;

import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.Task;
import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.enums.TaskStatus;
import com.sdu.open.source.site.repository.TaskClassDao;
import com.sdu.open.source.site.repository.TaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaskService {

    private TaskClassDao  taskClassDao;
    private TaskDao taskDao;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public void setTaskClassDao(TaskClassDao taskClassDao) {
        this.taskClassDao = taskClassDao;
    }
    @Autowired
    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public List<TaskClass> getTaskClassList() {
        return taskClassDao.selectAll();
    }


    public List<Task> getTasksByCollectionUser(String collectionUser) {
        return taskDao.findByCollectionUser(collectionUser);
    }

    public List<Task> getTasksByCategoryId(Long categoryId) {
        return taskDao.findByTaskClass(categoryId);
    }

    public int getUserTaskCount(String collectionUser, Integer taskStatus) {
        return taskDao.userTaskCount(collectionUser, taskStatus);
    }

    public Task findById(Long taskId) {
        return taskDao.findById(taskId);
    }

    public void updateTask(RequestParamDTO param) {
        Task task = new Task();
        task.setCollectionUser(param.getUsername());
        task.setTaskStatus(TaskStatus.RECEIVED.getCode());
        task.setId(param.getTaskId());
        task.setUpdateTime(LocalDateTime.now().format(formatter));
        task.setCollectionTime(LocalDateTime.now().format(formatter));
        taskDao.update(task);
    }

    public TaskClass getClassNameById(Long id) {
        return taskClassDao.findById(id);
    }
}
