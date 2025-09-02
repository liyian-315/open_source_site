package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.Mirror;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MirrorDao {
    List<Mirror> selectAll();
}
