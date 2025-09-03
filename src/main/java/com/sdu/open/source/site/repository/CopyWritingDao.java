package com.sdu.open.source.site.repository;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-09-01  21:34
 * @Version: 1.0
 */
@Mapper
public interface CopyWritingDao {
    String selectCopyWritingTextByArea(String area);
}
