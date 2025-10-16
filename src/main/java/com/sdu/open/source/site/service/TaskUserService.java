package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.TaskUser;
import com.sdu.open.source.site.repository.TaskUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class TaskUserService {
    private TaskUserDao taskUserDao;

    @Autowired
    public void setTaskUserDao(TaskUserDao taskUserDao) {
        this.taskUserDao = taskUserDao;
    }

    public boolean relationExists(Long taskId, Long userId) {
        return taskUserDao.exists(taskId, userId) > 0;
    }

    public void addRelation(Long taskId, Long userId, Integer status, String collectionTime, String createTime) {
        TaskUser tu = new TaskUser();
        tu.setTaskId(taskId);
        tu.setUserId(userId);
        tu.setTaskStatus(status);
        tu.setCollectionTime(collectionTime);
        tu.setCreateTime(createTime);
        taskUserDao.insert(tu);
    }

    public int countActiveByUserId(Long userId) {
        return taskUserDao.countActiveByUserId(userId);
    }

    public List<Map<String, Object>> findTasksByUserId(Long userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return taskUserDao.findTasksByUserId(userId, offset, pageSize);
    }

    public Long countTasksByUserId(Long userId) {
        return taskUserDao.countTasksByUserId(userId);
    }

    public int updateResultLinkByTaskAndUser(Long taskId, Long userId, String resultLink) {
        return taskUserDao.updateResultLinkByTaskAndUser(taskId, userId, resultLink);
    }

}
