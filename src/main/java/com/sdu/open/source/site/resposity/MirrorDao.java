package com.sdu.open.source.site.resposity;

import com.sdu.open.source.site.entity.Mirror;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
*@Author: liyian
*@Description: 
*@CreateTime: 2025-08-26  09:41 
*@Version: 1.0
*/
@Mapper
public interface MirrorDao {
    List<Mirror> selectAll();
}
