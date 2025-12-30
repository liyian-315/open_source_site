package com.sdu.open.source.site.entity;

import lombok.Data;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-09-01  18:58
 * @Version: 1.0
 */
@Data
public class Menu {
    private Long id;
    private Long level;  // 父级目录的ID，一级目录为0
    private String title;
    private String description;
    private String url;
    private String icon;
    private Integer order;
}
