package com.sdu.open.source.site.enums;

import lombok.Getter;

/**
 * 任务状态枚举
 */
@Getter
public enum TaskStatus {
    /**
     * 待领取
     */
    TO_BE_RECEIVED(1, "待领取"),
    /**
     * 已领取
     */
    RECEIVED(2, "已领取"),
    /**
     * 已完成
     */
    COMPLETED(3, "已完成");

    private final Integer code;
    private final String desc;

    TaskStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static TaskStatus getByCode(Integer code) {
        for (TaskStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
