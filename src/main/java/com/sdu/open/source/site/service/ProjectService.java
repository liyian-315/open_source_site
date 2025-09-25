package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.Project;
import com.sdu.open.source.site.entity.Tag;
import com.sdu.open.source.site.entity.ProjectTag;
import com.sdu.open.source.site.repository.ProjectDao;
import com.sdu.open.source.site.repository.TagDao;
import com.sdu.open.source.site.repository.ProjectTagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author: liyian
 * @Description: 项目服务实现类
 * @CreateTime: 2025-09-23  20:20
 * @Version: 1.0
 */
@Service
public class ProjectService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProjectDao projectDao;
    private final TagDao tagDao;
    private final ProjectTagDao projectTagDao;

    @Autowired
    public ProjectService(ProjectDao projectDao, TagDao tagDao, ProjectTagDao projectTagDao) {
        this.projectDao = projectDao;
        this.tagDao = tagDao;
        this.projectTagDao = projectTagDao;
    }

    public List<Project> getAllProjects() {
        return projectDao.selectAll();
    }

    public Project getProjectById(Long id) {
        return projectDao.selectById(id);
    }

    public Project createProject(Project project) {
        project.setCreateTime(LocalDateTime.now().format(formatter));
        projectDao.insert(project);
        return project;
    }

    public List<Tag> getProjectTags(Long projectId) {
        return tagDao.selectByProjectId(projectId);
    }

    public List<Tag> getAllTags() {
        return tagDao.selectAll();
    }

    public boolean addProjectTag(Long projectId, Long tagId) {
        ProjectTag projectTag = new ProjectTag();
        projectTag.setProjectId(projectId);
        projectTag.setTagId(tagId);

        // 检查关联是否已存在
        ProjectTag existing = projectTagDao.selectByProjectAndTagId(projectId, tagId);
        if (existing != null) {
            return false;
        }

        projectTagDao.insert(projectTag);
        return true;
    }
}
