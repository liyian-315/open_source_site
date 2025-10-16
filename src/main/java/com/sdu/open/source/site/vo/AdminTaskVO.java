package com.sdu.open.source.site.vo;

import lombok.Data;

@Data
public class AdminTaskVO {
    private Long taskUserId;
    private String taskName; // 任务名称
    private String taskClassName; // 分类名
    private String collectionUser; // 领取人 name
    private String phone;
    private String email;
    private String email2;
    private String collectionTime; // 任务领取时间
    private String createTime; // 任务创建时间
    private String deadlineTime; // 任务截止时间
    private Integer taskStatus; // 任务状态：1 审核中 2 进行中 3 结束 4 关闭
    private String resultLink; // 任务结果链接
}
