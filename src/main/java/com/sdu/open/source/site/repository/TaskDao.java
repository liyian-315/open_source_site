package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.Task;
import com.sdu.open.source.site.entity.TaskClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskDao {
    /**
     * 根据领取人姓名查询该用户下所有的任务
     * @param collectionUser 领取人姓名
     * @return 任务列表
     */
    List<Task> findByCollectionUser(String collectionUser);

    /**
     * 根据任务分类id查询该分类下的所有任务
     * @param taskClass 任务分类id
     * @return 任务列表
     */
    List<Task> findByTaskClass(Long taskClass);

    /**
     * 根据领取人姓名和任务状态查询该用户下的任务
     * @param collectionUser 领取人姓名
     *
     * @return 任务个数
     */
    int userTaskCount(@Param("collectionUser") String collectionUser,  @Param("taskStatus") Integer taskStatus);

    Task findById(Long taskId);

    void update(Task task);
}
