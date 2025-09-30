package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.ProjectDetailDTO;
import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.entity.Project;
import com.sdu.open.source.site.entity.Tag;
import com.sdu.open.source.site.service.ProjectService;
import com.sdu.open.source.site.vo.ProjectListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/api/projects")
    public ResponseEntity<ProjectListVO> getAllProjects(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<Project> projects = projectService.getProjectsByPage(offset, pageSize);
            Long total = projectService.getTotalProjects();

            return getProjectListVOResponseEntity(pageNum, pageSize, total, projects);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/projects/{id}")
    public ResponseEntity<ProjectDetailDTO> getProjectById(@PathVariable("id") Long id) {
        try {
            ProjectDetailDTO projectDetail = projectService.getProjectDetail(id);
            if (projectDetail == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(projectDetail, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting project by id: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/projects/{id}/tags")
    public ResponseEntity<List<Tag>> getProjectTags(@PathVariable("id") Long projectId) {
        try {
            List<Tag> tags = projectService.getProjectTags(projectId);
            if (tags == null || tags.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tags, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/projects/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        try {
            List<Tag> tags = projectService.getAllTags();
            if (tags == null || tags.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tags, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<ProjectListVO> getProjectListVOResponseEntity(
            Integer pageNum, Integer pageSize, Long total, List<Project> projectList) {
        ProjectListVO result = new ProjectListVO();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setPages(total == 0 ? 0 : (int) (total + pageSize - 1) / pageSize);
        result.setProjectList(projectList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/api/admin/project/addProjectTag")
    public ResponseEntity<Void> addProjectTag(@RequestBody RequestParamDTO requestParamDTO) {
        try {
            boolean success = projectService.addProjectTag(requestParamDTO.getProjectId(), requestParamDTO.getTagIds());
            if (success) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/admin/project/createProject")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        try {
            Project createdProject = projectService.createProject(project);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/admin/project/addTag")
    public ResponseEntity<Void> addTag(@RequestBody Tag tag) {
        try {
            if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            boolean success = projectService.addNewTag(tag);
            if (success) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("添加新标签失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 项目图片文案批量插入
     *
     * @param requestParamDTO
     * @return
     */
    @PostMapping("/api/admin/project/addProjectCW")
    public ResponseEntity<Void> addProjectCW(@RequestBody RequestParamDTO requestParamDTO) {
        try {
            Long projectId = requestParamDTO.getProjectId();
            List<CopyWriting> cwList = requestParamDTO.getCwList();
            if (cwList == null || cwList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (projectId == null || projectId <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (requestParamDTO.getCwType() == null || requestParamDTO.getCwType().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (projectService.addProjectDisplay(requestParamDTO)) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新项目基础信息（名称、描述、Git仓库等）
     */
    @PutMapping("/api/admin/project/updateProject")
    public ResponseEntity<Project> updateProject(@RequestBody RequestParamDTO updateParam) {
        try {
            if (updateParam.getProjectId() == null || updateParam.getProjectId() <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Project updatedProject = projectService.updateProjectBaseInfo(updateParam);
            if (updatedProject == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        } catch (Exception e) {
            log.error("更新项目基础信息失败，projectId: {}", updateParam.getProjectId(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据projectId全量更新标签（先删原有关联，再新增）
     */
    @PutMapping("/api/admin/project/updateProjectTags")
    public ResponseEntity<Void> updateProjectTags(@RequestBody RequestParamDTO updateParam) {
        try {
            if (updateParam.getProjectId() == null || updateParam.getProjectId() <= 0 || updateParam.getTagIds() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            boolean success = projectService.updateProjectTags(updateParam.getProjectId(), updateParam.getTagIds());
            if (!success) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("更新项目标签失败，projectId: {}", updateParam.getProjectId(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * PUT /api/admin/project/updateProjectCW
     */
    @PutMapping("/api/admin/project/updateProjectCW")
    public ResponseEntity<Void> updateProjectCW(@RequestBody RequestParamDTO updateParam) {
        try {
            if (updateParam.getProjectId() == null || updateParam.getProjectId() <= 0
                    || updateParam.getCwList() == null || updateParam.getCwList().isEmpty()
                    || updateParam.getCwType() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            boolean success = projectService.updateProjectCopyWriting(updateParam);
            if (!success) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("更新项目文案失败，projectId: {}", updateParam.getProjectId(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}