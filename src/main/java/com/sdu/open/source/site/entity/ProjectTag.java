package com.sdu.open.source.site.entity;

import lombok.Data;

/**
 * @Author: liyian
 * @Description: 项目与标签的关联关系
 * @CreateTime: 2025-09-23  10:17
 * @Version: 1.0
 */
@Data
public class ProjectTag {
    private Long projectId;
    private Long tagId;
}
