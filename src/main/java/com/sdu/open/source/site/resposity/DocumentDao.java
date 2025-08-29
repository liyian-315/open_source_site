package com.sdu.open.source.site.resposity;

import com.sdu.open.source.site.entity.Document;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-08-25  16:48
 * @Version: 1.0
 */
@Mapper
public interface DocumentDao {
    List<Document> selectAll(Document document);
}
