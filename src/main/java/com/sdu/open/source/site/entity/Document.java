package com.sdu.open.source.site.entity;

import lombok.Data;

@Data
public class Document {
    private Long id;
    private String title;
    private String url;
    private String arch;
    private String manufacturer;
    private String series;
}
