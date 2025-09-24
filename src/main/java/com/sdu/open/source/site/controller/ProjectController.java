package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.Project;
import com.sdu.open.source.site.entity.Tag;
import com.sdu.open.source.site.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            if (projects == null || projects.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") Long id) {
        try {
            Project project = projectService.getProjectById(id);
            if (project == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(project, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        try {
            Project createdProject = projectService.createProject(project);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/tags")
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

    @GetMapping("/tags")
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

    @PostMapping("/{id}/tags")
    public ResponseEntity<Void> addProjectTag(@PathVariable("id") Long projectId, @RequestBody Tag tag) {
        try {
            boolean success = projectService.addProjectTag(projectId, tag.getId());
            if (success) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
