package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.ProjectTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectTagDao {
    void insert(ProjectTag projectTag);
    ProjectTag selectByProjectAndTagId(@Param("projectId") Long projectId, @Param("tagId") Long tagId);
    void deleteByProjectId(Long projectId);
    void deleteByTagId(Long tagId);
}
