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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private TaskClassDao taskClassDao;
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


    public List<Task> getTasksByCollectionUser(String collectionUser, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Map<String, Object> params = new HashMap<>();
        params.put("collectionUser", collectionUser);
        params.put("offset", offset);
        params.put("pageSize", pageSize);

        return taskDao.findByCollectionUser(params);
    }

    public List<Task> getTasksByCategoryId(Long categoryId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Map<String, Object> params = new HashMap<>();
        params.put("taskClass", categoryId);
        params.put("offset", offset);
        params.put("pageSize", pageSize);

        return taskDao.findByTaskClass(params);
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

    /**
     * 统计分类下任务总数
     *
     * @param categoryId
     * @return
     */
    public Long countTasksByCategoryId(Long categoryId) {
        return taskDao.countTasksByCategoryId(categoryId);
    }

    /**
     * 统计用户下任务总数
     *
     * @param user
     * @return
     */
    public Long countTasksByCollectionUser(String user) {
        return taskDao.countTasksByCollectionUser(user);
    }
}
