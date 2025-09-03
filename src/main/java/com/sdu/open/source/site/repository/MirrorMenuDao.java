package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-09-01  19:54
 * @Version: 1.0
 */
@Mapper
public interface MirrorMenuDao {
    List<Menu> selectAll();
}
