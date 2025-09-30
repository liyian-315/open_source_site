package com.sdu.open.source.site.vo;

import com.sdu.open.source.site.entity.Project;
import lombok.Data;

import java.util.List;

@Data
public class ProjectListVO {
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private Integer pages;
    private List<Project> projectList;
}