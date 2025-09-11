package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.TaskClass;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskClassDao {
    List<TaskClass> selectAll();

    TaskClass findById(Long id);
}
