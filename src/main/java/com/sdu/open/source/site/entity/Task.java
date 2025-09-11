package com.sdu.open.source.site.entity;

import lombok.Data;

@Data
public class Task {
    private Long id; // 主键自增 id
    private String taskName; // 任务名称
    private Long taskClass; // 任务分类 id
    private String taskClassName; // 分类名
    private String taskDescription; // 任务详细描述
    private Integer taskStatus; // 任务状态，1 - 待领取，2 - 已领取，3 - 已完成
    private String collectionUser; // 领取人 name
    private String collectionTime; // 任务领取时间
    private String createTime; // 任务创建时间
    private String updateTime; // 任务更新时间
}
