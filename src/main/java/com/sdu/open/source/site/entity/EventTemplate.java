package com.sdu.open.source.site.entity;
import lombok.Data;

@Data
public class EventTemplate {
    private String id;
    private String name;
    private String blocks;  // JSON
    private Boolean enabled;
    private Integer version;
}
