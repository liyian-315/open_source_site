package com.sdu.open.source.site.repository;
import com.sdu.open.source.site.entity.EventTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EventTemplateDao {
    EventTemplate selectById(@Param("id") String id);
}
