package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagDao {
    List<Tag> selectAll();
    Tag selectById(Long id);
    List<Tag> selectByProjectId(Long projectId);
    void insert(Tag tag);
    Tag selectByName(String name);
}
