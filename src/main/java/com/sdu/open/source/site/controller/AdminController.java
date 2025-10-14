package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.service.UserService;
import com.sdu.open.source.site.vo.PageResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/admin/update_user")
    public ResponseEntity<?> updateUser(@RequestBody RequestParamDTO param) {
        try {
            userService.updateUserByParam(param);
            return new ResponseEntity<>("更新用户成功", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("更新用户失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/select_user")
    public ResponseEntity<?> selectUser(@RequestBody RequestParamDTO param) {
        try {
            PageResultVO<User> pageResult = userService.selectByUsernameOrFullname(param);
            return new ResponseEntity<>(pageResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("用户查询失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/delete_user")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        if (id == null) {
            return new ResponseEntity<>("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }
        try {
            boolean deleteSuccess = userService.deleteUserById(id);
            if (deleteSuccess) {
                log.info("用户删除成功：用户ID={}", id);
                return new ResponseEntity<>("用户删除成功", HttpStatus.OK); // 200成功
            } else {
                log.warn("用户删除失败：未找到该用户，用户ID={}", id);
                return new ResponseEntity<>("删除失败：未找到ID为" + id + "的用户", HttpStatus.NOT_FOUND); // 404用户不存在
            }
        } catch (Exception e) {
            return new ResponseEntity<>("删除用户失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
