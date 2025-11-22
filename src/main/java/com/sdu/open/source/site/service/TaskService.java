package com.sdu.open.source.site.service;

import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.Task;
import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.entity.TaskUser;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.enums.TaskStatus;
import com.sdu.open.source.site.repository.TaskClassDao;
import com.sdu.open.source.site.repository.TaskDao;
import com.sdu.open.source.site.repository.TaskUserDao;
import com.sdu.open.source.site.repository.UserDao;
import com.sdu.open.source.site.vo.AdminTaskVO;
import com.sdu.open.source.site.vo.PageResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private TaskClassDao taskClassDao;
    private TaskDao taskDao;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserDao userDao;
    private final TaskUserDao taskUserDao;

    public TaskService(UserDao userDao, TaskUserDao taskUserDao) {
        this.userDao = userDao;
        this.taskUserDao = taskUserDao;
    }

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

    /**
     * 条件查询任务
     *
     * @param param
     * @return
     */
    public PageResultVO<AdminTaskVO> selectByParam(RequestParamDTO param) {
        int pageNum = param.getPageNum() != null ? param.getPageNum() : 1;
        int pageSize = param.getPageSize() != null ? param.getPageSize() : 10;
        int start = (pageNum - 1) * pageSize;

        List<AdminTaskVO> taskVOList = taskDao.selectTaskVOByJoin(param, start, pageSize);
        long total = taskDao.countTaskVOByJoin(param);
        return new PageResultVO<>(taskVOList, total);
    }

    public boolean createTask(RequestParamDTO param) {
        Task task = new Task();
        task.setTaskName(param.getTaskName());
        task.setTaskClass(param.getTaskClass());
        task.setTaskProtocolId(param.getTaskProtocolId());
        task.setTaskDescription(param.getTaskDescription());
        task.setCollectionUser(param.getCollectionUser());
        task.setDeadlineTime(param.getDeadlineTime());
        task.setGiteeLink(param.getGiteeLink());
        task.setCreateTime(LocalDateTime.now().format(formatter));
        task.setUpdateTime(LocalDateTime.now().format(formatter));
        return taskDao.insert(task) > 0;
    }

    public boolean updateTaskClass(RequestParamDTO param) {
        if (param.getTaskClass() == null) {
            throw new IllegalArgumentException("任务分类ID不能为空");
        }
        TaskClass taskClass = taskClassDao.findById(param.getTaskClass());
        if (taskClass == null) {
            throw new RuntimeException("任务分类不存在，分类ID：" + param.getTaskClass());
        }
        try {
            taskClass.setTaskCount(String.valueOf(Integer.parseInt(taskClass.getTaskCount())+1));
        } catch (Exception e) {
            throw new RuntimeException("任务数更新失败：" + e.getMessage());
        }
        return taskClassDao.update(taskClass) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createTaskWithClass(RequestParamDTO param) {
        if (param == null || param.getTaskName() == null || param.getTaskClass() == null) {
            throw new IllegalArgumentException("任务名称、分类ID不能为空");
        }
        boolean createSuccess = createTask(param);
        if (!createSuccess) {
            throw new RuntimeException("任务创建失败");
        }
        boolean updateSuccess = updateTaskClass(param);
        if (!updateSuccess) {
            throw new RuntimeException("分类任务数更新失败");
        }
        return true;
    }
}
