package com.sdu.open.source.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdu.open.source.site.dto.ProjectDetailDTO;
import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.entity.Project;
import com.sdu.open.source.site.entity.Tag;
import com.sdu.open.source.site.entity.ProjectTag;
import com.sdu.open.source.site.enums.CopyWritingAreas;
import com.sdu.open.source.site.repository.CopyWritingDao;
import com.sdu.open.source.site.repository.ProjectDao;
import com.sdu.open.source.site.repository.TagDao;
import com.sdu.open.source.site.repository.ProjectTagDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liyian
 * @Description: 项目服务实现类
 * @CreateTime: 2025-09-23  20:20
 * @Version: 1.0
 */
@Service
@Slf4j
public class ProjectService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProjectDao projectDao;
    private final TagDao tagDao;
    private final ProjectTagDao projectTagDao;
    private final CopyWritingDao copyWritingDao;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProjectService(ProjectDao projectDao, TagDao tagDao, ProjectTagDao projectTagDao, CopyWritingDao copyWritingDao, ObjectMapper objectMapper) {
        this.projectDao = projectDao;
        this.tagDao = tagDao;
        this.projectTagDao = projectTagDao;
        this.copyWritingDao = copyWritingDao;
        this.objectMapper = objectMapper;
    }

    public ProjectDetailDTO getProjectDetail(Long id) {
        Project project = projectDao.selectById(id);
        if (project == null) {
            return null;
        }

        ProjectDetailDTO dto = new ProjectDetailDTO();
        BeanUtils.copyProperties(project, dto);

        // 处理项目展示区域文案
        String projectDisplayArea = CopyWritingAreas.PROJECT_DISPLAY.getCode() + id;
        List<CopyWriting> projectDisplays = copyWritingDao.selectListByArea(projectDisplayArea);
        dto.setProjectDisplays(projectDisplays);

        // 处理学习资料区域文案
        String learningMaterialArea = CopyWritingAreas.LEARNING_MATERIAL.getCode() + id;
        List<CopyWriting> learningMaterials = copyWritingDao.selectListByArea(learningMaterialArea);
        dto.setLearningMaterials(learningMaterials);
        // 动态设置 moduleDisplay
        Map<String, Boolean> moduleDisplayMap = getStringBooleanMap(project, projectDisplays, learningMaterials);

        try {
            dto.setModuleDisplay(objectMapper.valueToTree(moduleDisplayMap));
        } catch (Exception e) {
            log.error("Failed to convert moduleDisplayMap to JsonNode", e);
            // 若转换失败，设置默认值
            Map<String, Boolean> defaultMap = new HashMap<>();
            defaultMap.put("gitRepo", true);
            defaultMap.put("projectIntro", true);
            defaultMap.put("projectDisplay", false);
            defaultMap.put("learningMaterial", false);
            dto.setModuleDisplay(objectMapper.valueToTree(defaultMap));
        }

        return dto;
    }

    private static Map<String, Boolean> getStringBooleanMap(Project project, List<CopyWriting> projectDisplays, List<CopyWriting> learningMaterials) {
        Map<String, Boolean> moduleDisplayMap = new HashMap<>();
        moduleDisplayMap.put("gitRepo", project.getGitRepo() != null && !project.getGitRepo().trim().isEmpty());
        moduleDisplayMap.put("projectIntro", project.getProjectIntro() != null && !project.getProjectIntro().trim().isEmpty());
        moduleDisplayMap.put("projectDisplay", projectDisplays != null && !projectDisplays.isEmpty());
        moduleDisplayMap.put("learningMaterial", learningMaterials != null && !learningMaterials.isEmpty());
        return moduleDisplayMap;
    }

    public Project createProject(Project project) throws JsonProcessingException {
        project.setCreateTime(LocalDateTime.now().format(formatter));
        if(project.getModuleDisplay() == null){

            // 将JSON字符串解析为JsonNode
            JsonNode defaultModuleDisplay = objectMapper.readTree(
                    "{\"gitRepo\": true, \"projectIntro\": true, \"projectDisplay\": false, \"learningMaterial\": false}"
            );
            // 设置默认值
            project.setModuleDisplay(defaultModuleDisplay);
        }
        projectDao.insert(project);
        return project;
    }

    public List<Tag> getProjectTags(Long projectId) {
        return tagDao.selectByProjectId(projectId);
    }

    public List<Tag> getAllTags() {
        return tagDao.selectAll();
    }

    public boolean addProjectTag(Long projectId, List<Long> tagIds) {
        ProjectTag projectTag = new ProjectTag();
        projectTag.setProjectId(projectId);
        for (Long tagId : tagIds) {
            projectTag.setTagId(tagId);
            // 检查关联是否已存在
            ProjectTag existing = projectTagDao.selectByProjectAndTagId(projectId, tagId);
            if (existing != null) {
                continue;
            }
            projectTagDao.insert(projectTag);
        }
        return true;
    }

    public List<Project> getProjectsByPage(int offset, int pageSize) {
        return projectDao.selectByPage(offset, pageSize);
    }
    public Long getTotalProjects() {
        return projectDao.selectTotalCount();
    }

    @Transactional
    public boolean addNewTag(Tag tag) {
        try {
            //检查标签是否已存在
             Tag existingTag = tagDao.selectByName(tag.getName());
             if (existingTag != null) {
                 return false;
             }
            tagDao.insert(tag);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("添加标签失败", e);
        }
    }

    public boolean addProjectDisplay(RequestParamDTO param) {
        if (CopyWritingAreas.PROJECT_DISPLAY.getCode().equals(param.getCwType())){
            List<CopyWriting> cwList = param.getCwList();
            for (CopyWriting cw : cwList) {
                cw.setArea(CopyWritingAreas.PROJECT_DISPLAY.getCode()+param.getProjectId().toString());
                copyWritingDao.insert(cw);
            }
            return true;
        }
        if (CopyWritingAreas.LEARNING_MATERIAL.getCode().equals(param.getCwType())){
            List<CopyWriting> cwList = param.getCwList();
            for (CopyWriting cw : cwList) {
                cw.setArea(CopyWritingAreas.PROJECT_DISPLAY.getCode()+param.getProjectId().toString());
                copyWritingDao.insert(cw);
            }
            return true;
        }
        return false;
    }

    /**
     * 更新项目基础信息（非null字段才更新）
     */
    public Project updateProjectBaseInfo(RequestParamDTO  param) {
        Project existingProject = projectDao.selectById(param.getProjectId());
        if (existingProject == null) {
            log.warn("更新项目基础信息失败：项目不存在，projectId: {}", param.getProjectId());
            return null;
        }
        if (param.getName() != null && !param.getName().trim().isEmpty()) {
            existingProject.setName(param.getName().trim());
        }
        if (param.getDescription() != null) {
            existingProject.setDescription(param.getDescription());
        }
        if (param.getGitRepo() != null) {
            existingProject.setGitRepo(param.getGitRepo());
        }
        if (param.getProjectIntro() != null) {
            existingProject.setProjectIntro(param.getProjectIntro());
        }
        projectDao.update(existingProject);
        return projectDao.selectById(param.getProjectId());
    }

    /**
     * 2. 全量更新项目标签（先删除原有关联，再新增）
     */
    @Transactional
    public boolean updateProjectTags(Long projectId, List<Long> newTagIds) {
        Project existingProject = projectDao.selectById(projectId);
        if (existingProject == null) {
            log.warn("更新项目标签失败：项目不存在，projectId: {}", projectId);
            return false;
        }
        projectTagDao.deleteByProjectId(projectId);
        log.info("已删除项目原有标签关联，projectId: {}", projectId);
        addProjectTag(projectId, newTagIds);
        log.info("已新增项目标签关联，projectId: {}, 新标签数量: {}", projectId, newTagIds.size());

        return true;
    }

    /**
     * 3. 全量更新项目文案（先删除原有文案，再批量新增）
     */
    @Transactional
    public boolean updateProjectCopyWriting(RequestParamDTO param) {
        Long projectId = param.getProjectId();
        List<CopyWriting> newCwList = param.getCwList();
        String cwType = param.getCwType();
        Project existingProject = projectDao.selectById(projectId);
        if (existingProject == null) {
            log.warn("更新项目文案失败：项目不存在，projectId: {}", projectId);
            return false;
        }
        String cwArea;
        if (CopyWritingAreas.PROJECT_DISPLAY.getCode().equals(cwType)) {
            cwArea = CopyWritingAreas.PROJECT_DISPLAY.getCode() + projectId;
        } else if (CopyWritingAreas.LEARNING_MATERIAL.getCode().equals(cwType)) {
            cwArea = CopyWritingAreas.LEARNING_MATERIAL.getCode() + projectId;
        } else {
            log.error("更新项目文案失败：无效的文案类型，cwType: {}", cwType);
            throw new IllegalArgumentException("无效的文案类型");
        }
        copyWritingDao.deleteByArea(cwArea);
        log.info("已删除项目原有文案，projectId: {}, 文案区域: {}", projectId, cwArea);
        for (CopyWriting cw : newCwList) {
            cw.setArea(cwArea); // 确保文案区域正确
            copyWritingDao.insert(cw);
        }
        log.info("已新增项目文案，projectId: {}, 新文案数量: {}", projectId, newCwList.size());

        return true;
    }
}
