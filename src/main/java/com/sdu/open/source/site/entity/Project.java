package com.sdu.open.source.site.entity;

import lombok.Data;

/**
 * @Author: liyian
 * @Description: 开源项目
 * @CreateTime: 2025-09-23  10:15
 * @Version: 1.0
 */
@Data
public class Project {
    private Long id;
    private String name;
    private String description;
    private String createTime;
}

