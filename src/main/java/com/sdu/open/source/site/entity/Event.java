package com.sdu.open.source.site.entity;

import lombok.Data;

@Data
public class Event {
    private Long id;
    private String slug;
    private String title;
    private String summary;
    private String coverUrl;
    private String type;
    private Integer status;

    private String startTime;
    private String endTime;

    private String city;
    private String location;
    private Boolean online;
    private String tags;        // 逗号分隔
    private String templateId;

    private String blocks;      // JSON
    private String contentMd;   // Markdown 字符串
    private String speakers;    // JSON
    private String agenda;      // JSON
    private String gallery;     // JSON

    private String ctaText;
    private String ctaUrl;
    private String seo;         // JSON
    private Integer viewCount;

    private String detailUrl;        // 为空则进入 /events/:slug
    private Boolean detailIsExternal; // true=外链新开窗
}
