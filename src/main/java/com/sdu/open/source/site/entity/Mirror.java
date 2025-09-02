package com.sdu.open.source.site.entity;

import lombok.Data;

@Data
public class Mirror {
    private Long id;
    private String name;
    private String url;
    private String size;
    private String time;
    private String arch;
    private String manufacturer;
    private String series;
}
