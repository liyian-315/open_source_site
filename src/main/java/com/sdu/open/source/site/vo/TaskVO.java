package com.sdu.open.source.site.vo;

import lombok.Data;

@Data
public class TaskVO {
    private Long id;
    private String taskName; // 任务名称
    private String taskProtocolTitle; // 任务协议标题
    private String taskProtocolLink; // 任务协议链接
    private String taskClassName; // 分类名
    private String taskDescription; // 任务详细描述
    private Integer taskStatus; // 任务状态，1 - 待领取，2 - 已领取，3 - 已完成
    private String collectionUser; // 领取人 name
    private String collectionTime; // 任务领取时间
    private String createTime; // 任务创建时间
    private String updateTime; // 任务更新时间
}