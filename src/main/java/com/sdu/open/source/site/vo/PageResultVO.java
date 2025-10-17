package com.sdu.open.source.site.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResultVO<T> {
    private List<T> list;
    private Long total;

    public PageResultVO(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResultVO() {
    }
}
