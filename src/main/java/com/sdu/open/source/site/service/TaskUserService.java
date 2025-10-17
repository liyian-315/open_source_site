package com.sdu.open.source.site.service;

import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.TaskUser;
import com.sdu.open.source.site.repository.TaskUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class TaskUserService {
    private TaskUserDao taskUserDao;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        tu.setUpdateTime(createTime);
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


    public boolean update(@Valid RequestParamDTO param) {
        Assert.isTrue(param.getTaskStatus() >= 1 && param.getTaskStatus() <= 4,
                "任务状态不合法：仅支持 1（审核中）、2（进行中）、3（结束）、4（关闭）");
        TaskUser Tu = taskUserDao.selectByTaskUserId(param.getTaskUserId());
        if (Tu == null) {
            return false;
        }
        Tu.setTaskStatus(param.getTaskStatus());
        Tu.setUpdateTime(LocalDateTime.now().format(formatter));
        taskUserDao.updateById(Tu);
        return true;
    }
}
