package com.sdu.open.source.site.enums;

import lombok.Getter;

/**
 * 文案区域枚举类，用于标识不同模块的文案所在区域
 */
@Getter
public enum CopyWritingAreas {
    /**
     * 项目展示区域
     */
    PROJECT_DISPLAY("projectDisplay", "项目展示区域"),

    /**
     * 学习资料区域
     */
    LEARNING_MATERIAL("learningMaterial", "学习资料区域");

    private final String code;
    private final String description;

    CopyWritingAreas(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据编码获取枚举值
    public static CopyWritingAreas getByCode(String code) {
        for (CopyWritingAreas area : CopyWritingAreas.values()) {
            if (area.getCode().equals(code)) {
                return area;
            }
        }
        return null;
    }
}
