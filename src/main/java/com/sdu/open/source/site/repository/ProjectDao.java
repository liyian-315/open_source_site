package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectDao {
    List<Project> selectAll();
    Project selectById(Long id);
    void insert(Project project);
    void update(Project project);
    void deleteById(Long id);
    List<Project> selectByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    Long selectTotalCount();
}
