package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.TaskUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface TaskUserDao {
    int exists(@Param("taskId") Long taskId, @Param("userId") Long userId);
    void insert(TaskUser taskUser);
    int countActiveByUserId(@Param("userId") Long userId); // 状态=2

    // 我的任务（分页）
    List<Map<String, Object>> findTasksByUserId(@Param("userId") Long userId,
                                                @Param("offset") int offset,
                                                @Param("pageSize") int pageSize);

    Long countTasksByUserId(@Param("userId") Long userId);

    List<Long> findClaimedTaskIdsByUserIdAndTaskIds(
            @Param("userId") Long userId,
            @Param("taskIds") List<Long> taskIds
    );
}
