package com.sdu.open.source.site.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.sdu.open.source.site.entity.CopyWriting;
import lombok.Data;

import java.util.List;

/**
 * @Author: liyian
 * @Description: 项目详情数据传输对象，整合项目基本信息和展示内容
 * @CreateTime: 2025-09-25
 * @Version: 1.0
 */
@Data
public class ProjectDetailDTO {
    private Long id;
    private String name;
    private String description;
    private String createTime;
    private String gitRepo;
    private String projectIntro;
    // 详情中四个模块是否展示：例 {"name": true, "tags": true, "gitRepo": true, "projectIntro": true, "projectDisplay": true, "learningMaterial": true}，其中 true 表示对应模块展示，false 表示不展示。
    private JsonNode moduleDisplay;
    // 项目展示的图片和链接列表
    private List<CopyWriting> projectDisplays;
    // 项目展示的资料
    private List<CopyWriting> learningMaterials;
}