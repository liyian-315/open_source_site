package com.sdu.open.source.site.entity;

import lombok.Data;

/** 任务-用户 关系表（多对多，承载领取状态与时间） */
@Data
public class TaskUser {
    private Long id;
    private Long taskId;
    private Long userId;
    private Integer taskStatus;     // 2-已领取，3-已完成
    private String collectionTime;  // 领取时间
    private String createTime;      // 记录创建时间
}
