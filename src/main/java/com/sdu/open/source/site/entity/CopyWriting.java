package com.sdu.open.source.site.entity;

import lombok.Data;

/**
 * @Author: liyian
 * @Description: 文案
 * @CreateTime: 2025-09-01  21:26
 * @Version: 1.0
 */
@Data
public class CopyWriting {
    private Long id;
    private String area;
    private String title;
    private String copyWritingText;
    private String link;
    private String note;
}
