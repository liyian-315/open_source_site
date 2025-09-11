package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.Task;
import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.repository.TaskClassDao;
import com.sdu.open.source.site.repository.TaskDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private TaskClassDao  taskClassDao;
    private TaskDao taskDao;

    @Autowired
    public void setTaskClassDao(TaskClassDao taskClassDao) {
        this.taskClassDao = taskClassDao;
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
}
