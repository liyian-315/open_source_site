package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.ApiResponse;
import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.DsProtocol;
import com.sdu.open.source.site.entity.Task;
import com.sdu.open.source.site.entity.TaskClass;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.enums.TaskStatus;
import com.sdu.open.source.site.service.CopyWritingService;
import com.sdu.open.source.site.service.DsProtocolService;
import com.sdu.open.source.site.service.TaskService;
import com.sdu.open.source.site.service.UserService;
import com.sdu.open.source.site.vo.TaskListVO;
import com.sdu.open.source.site.vo.TaskVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/task")
@Slf4j
public class TaskController {

    private TaskService taskService;
    private UserService userService;
    private DsProtocolService dsProtocolService;
    private CopyWritingService  copyWritingService;
    // todo 后期改为配置在application.properties
    private static final String UPLOAD_BASE_DIR = "task-uploads/";

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProtocolService(DsProtocolService dsProtocolService) {
        this.dsProtocolService = dsProtocolService;
    }

    @Autowired
    public void setCopyWritingService(CopyWritingService copyWritingService) {
        this.copyWritingService = copyWritingService;
    }
    //返回当前用户已领取但未完成的任务数量
    @GetMapping("/fetchReceivedTaskCount")
    public ResponseEntity<?> fetchReceivedTaskCount(@RequestParam("params") String collectionUser) {
        try {
            int count = taskService.getUserTaskCount(collectionUser, TaskStatus.COMPLETED.getCode());
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            log.error("用户任务统计失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/fetchMyTasks")
    public ResponseEntity<TaskListVO> fetchMyTasks(
            @RequestParam("params") String collectionUser,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Long total = taskService.countTasksByCollectionUser(collectionUser);
            List<Task> tasks = taskService.getTasksByCollectionUser(collectionUser, pageNum, pageSize);
            Set<Long> dsProtocolIds = tasks.stream()
                    .map(Task::getTaskProtocolId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            final Map<Long, DsProtocol> dsProtocolMap;
            if (!dsProtocolIds.isEmpty()) {
                List<DsProtocol> dsProtocols = dsProtocolService.findByIds(dsProtocolIds);
                dsProtocolMap = dsProtocols.stream()
                        .collect(Collectors.toMap(DsProtocol::getId, p -> p));
            } else {
                dsProtocolMap = new HashMap<>();
            }

            List<TaskVO> taskVOList = tasks.stream()
                    .map(task -> {
                        TaskVO vo = new TaskVO();
                        BeanUtils.copyProperties(task, vo);
                        vo.setTaskClassName(taskService.getClassNameById(task.getTaskClass()).getName());
                        Long protocolId = task.getTaskProtocolId();
                        if (protocolId != null && dsProtocolMap.containsKey(protocolId)) {
                            DsProtocol dsProtocol = dsProtocolMap.get(protocolId);
                            vo.setTaskProtocolTitle(dsProtocol.getTitle());
                            vo.setTaskProtocolLink(dsProtocol.getLink());
                        }

                        return vo;
                    })
                    .collect(Collectors.toList());

            // 构建分页结果
            return getTaskListVOResponseEntity(pageNum, pageSize, total, taskVOList);
        } catch (Exception e) {
            log.error("我的任务查询失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<TaskListVO> getTaskListVOResponseEntity(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, Long total, List<TaskVO> taskVOList) {
        TaskListVO result = new TaskListVO();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setPages(total == 0 ? 0 : (int) (total + pageSize - 1) / pageSize);
        result.setTaskList(taskVOList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/fetchTasksByCategory")
    public ResponseEntity<TaskListVO> fetchTasksByCategory(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Long total = taskService.countTasksByCategoryId(categoryId);
            List<Task> tasks = taskService.getTasksByCategoryId(categoryId, pageNum, pageSize);
            Set<Long> dsProtocolIds = tasks.stream()
                    .map(Task::getTaskProtocolId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            final Map<Long, DsProtocol> DsProtocolMap;
            if (!dsProtocolIds.isEmpty()) {
                List<DsProtocol> DsProtocols = dsProtocolService.findByIds(dsProtocolIds);
                DsProtocolMap = DsProtocols.stream()
                        .collect(Collectors.toMap(DsProtocol::getId, p -> p));
            } else {
                DsProtocolMap = new HashMap<>();
            }
            List<TaskVO> taskVOList = tasks.stream()
                    .map(task -> {
                        TaskVO vo = new TaskVO();
                        BeanUtils.copyProperties(task, vo);
                        Long protocolId = task.getTaskProtocolId();
                        if (protocolId != null && DsProtocolMap.containsKey(protocolId)) {
                            DsProtocol dsProtocol = DsProtocolMap.get(protocolId);
                            vo.setTaskProtocolTitle(dsProtocol.getTitle());
                            vo.setTaskProtocolLink(dsProtocol.getLink());
                        }

                        return vo;
                    })
                    .collect(Collectors.toList());

            return getTaskListVOResponseEntity(pageNum, pageSize, total, taskVOList);
        } catch (Exception e) {
            log.error("分类下任务分页查询失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetchTaskCategories")
    public ResponseEntity<List<TaskClass>> fetchTaskCategories() {
        try {
            List<TaskClass> taskClassList = taskService.getTaskClassList();
            if (taskClassList == null || taskClassList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(taskClassList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("任务分类查询失败", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 领取任务
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PutMapping("/claimTask")
    public ResponseEntity<?> claimTask(@RequestBody RequestParamDTO param) throws Exception {
        try {
            // 获取请求参数
            String username = param.getUsername();
            Long taskId = param.getTaskId();
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(400, "用户名为空"));
            }
            if (taskId == null) {
                return ResponseEntity.ok(ApiResponse.error(400, "任务 ID 为空"));
            }
            User user = userService.findByUsername(username);
            if (user == null) {
                log.warn("任务领取失败，用户不存在: {}", username);
                return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
            }
            if (!user.getHasSignedPdf()) {
                return ResponseEntity.ok(ApiResponse.error(400, "用户未签署协议"));
            }

            // 验证任务是否存在
            Task task = taskService.findById(taskId);
            if (task == null) {
                log.warn("任务领取失败，任务不存在: {}", taskId);
                return ResponseEntity.ok(ApiResponse.error(404, "任务不存在"));
            }

            // 验证任务状态是否可领取
            if (!TaskStatus.TO_BE_RECEIVED.getCode().equals(task.getTaskStatus())) {
                log.warn("任务 {} 不可领取，当前状态: {}", taskId, task.getTaskStatus());
                return ResponseEntity.ok(ApiResponse.error(400, "任务不可领取，当前状态:"));
            }

            // 验证用户是否已领取该任务
            if (username.equals(task.getCollectionUser())) {
                return ResponseEntity.ok(ApiResponse.error(400, "您已领取该任务"));
            }

            // 验证用户领取任务数量是否超限
            int userTaskCount = taskService.getUserTaskCount(username, TaskStatus.COMPLETED.getCode());
            if (userTaskCount >= 2) {
                return ResponseEntity.ok(ApiResponse.error(400, "最多只能领取 2 个任务"));
            }

            taskService.updateTask(param);

            log.info("用户 {} 成功领取任务 {}", username, taskId);
            return ResponseEntity.ok(ApiResponse.success(null, "任务领取成功"));

        } catch (Exception e) {
            log.error("任务领取异常", e);
            return ResponseEntity.ok(ApiResponse.error(500, "任务领取失败，请稍后重试"));
        }
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadTaskFiles(
            @RequestParam("taskId") Long taskId,
            @RequestParam("files") MultipartFile[] files,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            String usernameHeader = request.getHeader("username");
            String username = null;

            if (usernameHeader != null && usernameHeader.startsWith("Bearer ")) {
                username = usernameHeader.substring(7).trim();
            } else if (usernameHeader != null) {
                username = usernameHeader.trim();
            }

            if (username == null || username.isEmpty()) {
                response.put("success", false);
                response.put("message", "用户名不能为空");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (files == null || files.length == 0) {
                response.put("success", false);
                response.put("message", "请选择要上传的文件");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            String folderName = username + "-" + taskId;

            String uploadBasePath = System.getProperty("user.dir") + File.separator + "task-uploads" + File.separator;
            Path uploadDir = Paths.get(uploadBasePath + folderName);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建上传目录: {}", uploadDir.toAbsolutePath());
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFilename = file.getOriginalFilename();
                    originalFilename = originalFilename.replace(File.separator, "").replace("/", "");
                    Path filePath = uploadDir.resolve(originalFilename);
                    file.transferTo(filePath);
                    log.info("文件上传成功: {}", filePath.toAbsolutePath());
                }
            }

            response.put("success", true);
            response.put("message", "文件上传成功");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("处理文件上传时发生错误", e);
            response.put("success", false);
            response.put("message", "处理错误: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getPublishTemplateUrl")
    public ResponseEntity<?> getPublishTemplateUrl() {
        try {
            Map<String, String> response = new HashMap<>();
            String wordUrl = copyWritingService.getCwByArea("word-template").getLink();
            response.put("url", wordUrl);

            log.info("获取成果发布模板地址成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取成果发布模板地址失败", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取模板地址失败：" + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
