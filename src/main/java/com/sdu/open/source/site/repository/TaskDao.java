package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.Task;
import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.vo.AdminTaskVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskDao {
    /**
     * 根据领取人姓名查询该用户下所有的任务
     *
     * @param params 领取人姓名
     * @return 任务列表
     */
    List<Task> findByCollectionUser(Map<String, Object> params);

    /**
     * 根据任务分类id查询该分类下的所有任务
     *
     * @param params 任务分类id
     * @return 任务列表
     */
    List<Task> findByTaskClass(Map<String, Object> params);

    /**
     * 根据领取人姓名和任务状态查询该用户下的任务
     *
     * @param collectionUser 领取人姓名
     * @return 任务个数
     */
    int userTaskCount(@Param("collectionUser") String collectionUser, @Param("taskStatus") Integer taskStatus);

    Task findById(Long taskId);

    void update(Task task);

    Long countTasksByCategoryId(Long categoryId);

    Long countTasksByCollectionUser(String user);

    long countTasksByParam(@Param("param") RequestParamDTO param);

    List<Task> selectTasksByParam(
            @Param("param") RequestParamDTO param,
            @Param("start") int start,
            @Param("pageSize") int pageSize
    );

    List<AdminTaskVO> selectTaskVOByJoin(
            @Param("param") RequestParamDTO param,
            @Param("start") int start,
            @Param("pageSize") int pageSize
    );

    long countTaskVOByJoin(@Param("param") RequestParamDTO param  );

    int insert(Task task);
}
