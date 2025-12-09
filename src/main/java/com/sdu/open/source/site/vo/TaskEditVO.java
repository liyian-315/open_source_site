package com.sdu.open.source.site.vo;

import lombok.Data;

@Data
public class TaskEditVO {
    private Long taskId; // 任务ID，用于编辑
    private String projectName; // 项目名（任务名称）
    private Long taskClassId; // 任务分类ID
    private String projectType; // 项目类（任务分类名称）
    private Long protocolId; // 协议ID
    private String protocol; // 协议（协议标题）
    private String gitee; // Gitee链接
    private String description; // 描述
    private String deadline; // 截止时间
}
