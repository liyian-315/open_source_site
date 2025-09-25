package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.dto.TypeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventMetaDao {
    List<TypeVO> selectTypes();
    List<String> selectHotTags();
}
