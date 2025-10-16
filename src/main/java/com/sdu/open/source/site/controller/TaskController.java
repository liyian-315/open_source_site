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
import com.sdu.open.source.site.service.TaskUserService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.sdu.open.source.site.service.TaskUserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/task")
@Slf4j
public class TaskController {

    private TaskService taskService;
    private UserService userService;
    private DsProtocolService dsProtocolService;
    private CopyWritingService  copyWritingService;
    private TaskUserService taskUserService;
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

    @Autowired
    public void setTaskUserService(TaskUserService taskUserService) {
        this.taskUserService = taskUserService;
    }

    @GetMapping("/fetchReceivedTaskCount")
    public ResponseEntity<?> fetchReceivedTaskCount(@RequestParam("params") String username) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) return new ResponseEntity<>(0, HttpStatus.OK);
            int count = taskUserService.countActiveByUserId(user.getId()); // 关系表状态=2
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/fetchMyTasks")
    public ResponseEntity<?> fetchMyTasks(@RequestParam("params") String username,
                                          @RequestParam("pageNum") String pageNumParam,
                                          @RequestParam("pageSize") String pageSizeParam) {
        try {
            int pageNum = Integer.parseInt(pageNumParam);
            int pageSize = Integer.parseInt(pageSizeParam);

            User user = userService.findByUsername(username);
            if (user == null) {
                return new ResponseEntity<>(ApiResponse.error(404, "用户不存在"), HttpStatus.OK);
            }

            Long total = taskUserService.countTasksByUserId(user.getId());
            List<Map<String, Object>> rows = taskUserService.findTasksByUserId(user.getId(), pageNum, pageSize);

            List<TaskVO> vos = rows.stream().map(r -> {
                TaskVO vo = new TaskVO();
                vo.setId(((Number) r.get("id")).longValue());
                vo.setTaskName((String) r.get("task_name"));

                // 分类名：优先用 task_class_name（没有时回退到ID字符串）
                Object className = r.get("task_class_name");
                if (className != null) {
                    vo.setTaskClassName(className.toString());
                } else {
                    Object cls = r.get("task_class");
                    if (cls != null) vo.setTaskClassName(cls.toString());
                }

                vo.setTaskDescription((String) r.get("task_description"));

                Object st = r.get("rel_task_status");
                if (st != null) vo.setTaskStatus(((Number) st).intValue()); // 状态来自关系表

                vo.setCollectionUser(username);

                Object ct = r.get("rel_collection_time");
                vo.setCollectionTime(ct == null ? null : ct.toString());

                Object ctime = r.get("create_time");
                vo.setCreateTime(ctime == null ? null : ctime.toString());

                Object utime = r.get("update_time");
                vo.setUpdateTime(utime == null ? null : utime.toString());

                Object dltime = r.get("deadline_time");
                vo.setDeadlineTime(dltime == null ? null : dltime.toString());

                // 协议名/链接
                Object ptitle = r.get("task_protocol_title");
                if (ptitle != null) vo.setTaskProtocolTitle(ptitle.toString());
                Object plink = r.get("task_protocol_link");
                if (plink != null) vo.setTaskProtocolLink(plink.toString());

                Object glink = r.get("gitee_link");
                if (glink != null) vo.setGiteeLink(glink.toString());

                Object recog = r.get("recog_status");
                if (recog != null) {
                    vo.setRecognitionStatus(Integer.parseInt(recog.toString()));
                }

                Object rlink = r.get("result_link");
                if (rlink != null) {
                    vo.setResultLink(rlink.toString());
                }
                return vo;
            }).collect(Collectors.toList());

            TaskListVO result = new TaskListVO();
            result.setTotal(total);
            result.setTaskList(vos);
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);

            return new ResponseEntity<>(ApiResponse.success("查询成功", result), HttpStatus.OK);
        } catch (Exception e) {
            log.error("fetchMyTasks failed", e);
            return new ResponseEntity<>(ApiResponse.error(500, "查询失败"), HttpStatus.OK);
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
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(value = "username", required = false) String username) {
        try {
            Long total = taskService.countTasksByCategoryId(categoryId);
            List<Task> tasks = taskService.getTasksByCategoryId(categoryId, pageNum, pageSize);

            // 协议映射：保持你原来的逻辑
            Set<Long> dsProtocolIds = tasks.stream()
                    .map(Task::getTaskProtocolId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            final Map<Long, DsProtocol> DsProtocolMap;
            if (!dsProtocolIds.isEmpty()) {
                List<DsProtocol> DsProtocols = dsProtocolService.findByIds(dsProtocolIds);
                DsProtocolMap = DsProtocols.stream().collect(Collectors.toMap(DsProtocol::getId, p -> p));
            } else {
                DsProtocolMap = new HashMap<>();
            }

            List<TaskVO> taskVOList = tasks.stream().map(task -> {
                TaskVO vo = new TaskVO();
                BeanUtils.copyProperties(task, vo);
                Long protocolId = task.getTaskProtocolId();
                if (protocolId != null && DsProtocolMap.containsKey(protocolId)) {
                    DsProtocol dsProtocol = DsProtocolMap.get(protocolId);
                    vo.setTaskProtocolTitle(dsProtocol.getTitle());
                    vo.setTaskProtocolLink(dsProtocol.getLink());
                    vo.setGiteeLink(task.getGiteeLink());
                }
                return vo;
            }).collect(Collectors.toList());

            // 打标“已领取”：仅判断 task_user 是否存在 (user_id, task_id) 记录（存在即已领取）
            if (username != null && !username.trim().isEmpty() && !taskVOList.isEmpty()) {
                User user = userService.findByUsername(username);
                if (user != null) {
                    for (TaskVO vo : taskVOList) {
                        if (vo.getId() != null && taskUserService.relationExists(vo.getId(), user.getId())) {
                            vo.setCollectionUser(username);
                            // if (vo.getTaskStatus() == null) vo.setTaskStatus(TaskStatus.RECEIVED.getCode());
                        }
                    }
                }
            }

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

    @PutMapping("/claimTask")
    public ResponseEntity<?> claimTask(@RequestBody RequestParamDTO param) throws Exception {
        try {
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
                return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
            }
            if (!user.getHasSignedPdf()) {
                return ResponseEntity.ok(ApiResponse.error(400, "需要先成为实习生"));
            }
            Task task = taskService.findById(taskId);
            if (task == null) {
                return ResponseEntity.ok(ApiResponse.error(404, "任务不存在"));
            }

            // 基于关系表防重复领取
            if (taskUserService.relationExists(taskId, user.getId())) {
                return ResponseEntity.ok(ApiResponse.error(400, "您已领取该任务"));
            }

            // 限制：已领取未完成 < 2
            int userTaskCount = taskUserService.countActiveByUserId(user.getId());
            if (userTaskCount >= 2) {
                return ResponseEntity.ok(ApiResponse.error(400, "最多只能领取 2 个任务"));
            }

            // 写入关系（状态=2 已领取）
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            taskUserService.addRelation(taskId, user.getId(), TaskStatus.RECEIVED.getCode(), now, now);

            return ResponseEntity.ok(ApiResponse.success(null, "任务领取成功"));
        } catch (Exception e) {
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
