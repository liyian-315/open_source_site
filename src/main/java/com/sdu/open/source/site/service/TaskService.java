package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.repository.TaskClassDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private TaskClassDao  taskClassDao;

    @Autowired
    public void setTaskClassDao(TaskClassDao taskClassDao) {
        this.taskClassDao = taskClassDao;
    }

    public List<TaskClass> getTaskClassList() {
        return taskClassDao.selectAll();
    }
}
