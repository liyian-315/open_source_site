package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.CopyWriting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-09-01  21:34
 * @Version: 1.0
 */
@Mapper
public interface CopyWritingDao {
    String selectCopyWritingTextByArea(String area);

    CopyWriting selectByArea(String area);

    List<CopyWriting> selectListByArea(@Param("area") String area);

    void insert(CopyWriting cw);
}
