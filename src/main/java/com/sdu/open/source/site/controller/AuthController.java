package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.ApiResponse;
import com.sdu.open.source.site.dto.JwtRequest;
import com.sdu.open.source.site.dto.JwtResponse;
import com.sdu.open.source.site.dto.RegisterRequest;
import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.security.JwtTokenUtil;
import com.sdu.open.source.site.service.CopyWritingService;
import com.sdu.open.source.site.service.UserDetailsServiceImpl;
import com.sdu.open.source.site.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    private CopyWritingService copyWritingService;

    @Autowired
    public void setCopyWritingService(CopyWritingService copyWritingService) {
        this.copyWritingService = copyWritingService;
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return JWT响应
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest request) {
        try {
            authenticate(request.getUsername(), request.getPassword());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);

            User user = userService.findByUsername(request.getUsername());
            return ResponseEntity.ok(ApiResponse.success(
                    new JwtResponse(token, user.getUsername(), user.getRole())
            ));
        } catch (Exception e) {
            log.error("登录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "登录失败: " + e.getMessage()));
        }
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // 检查用户名是否已存在
            if (userService.findByUsername(request.getUsername()) != null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "用户名已存在"));
            }

            // 创建新用户
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setEmail2(request.getEmail2());
            user.setFullname(request.getFullname());
            user.setPhone(request.getPhone());
            user.setAddress(request.getAddress());
            user.setCompany(request.getCompany());

            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(ApiResponse.success("注册成功", createdUser));
        } catch (Exception e) {
            log.error("注册失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "注册失败: " + e.getMessage()));
        }
    }

    /**
     * 认证用户
     *
     * @param username 用户名
     * @param password 密码
     * @throws Exception 认证异常
     */
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("用户已禁用", e);
        } catch (BadCredentialsException e) {
            throw new Exception("无效的凭据", e);
        }
    }

    @PostMapping("/personInfo")
    public ResponseEntity<?> getPersonInfo(@RequestBody RequestParamDTO param) throws Exception {
        try {
            String username = param.getUsername();
            User user = userService.findByUsername(username);
            if (user == null) {
                log.warn("获取用户个人信息失败：用户名 [{}] 不存在", username);
                return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
            }
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            log.error("获取用户个人信息系统异常，用户名：{}", param.getUsername(), e);
            return ResponseEntity.ok(ApiResponse.error(500, "获取个人信息失败，请稍后重试"));
        }
    }

    @PutMapping("/updatePersonInfo")
    public ResponseEntity<?> updatePersonInfo(@RequestBody RequestParamDTO param) throws Exception {
        try {
            User user = userService.findByUsername(param.getUsername());
            if (user == null) {
                log.warn("更新用户信息失败，用户不存在: {}", param.getUsername());
                return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
            }

            userService.updateUserByParam(param);

            return ResponseEntity.ok(ApiResponse.success(null, "个人信息更新成功"));

        } catch (Exception e) {
            log.error("更新用户信息异常，用户名: {}", param.getUsername(), e);
            return ResponseEntity.ok(ApiResponse.error(500, "更新个人信息失败，请稍后重试"));
        }
    }

    @GetMapping("/getPdfCW")
    public ResponseEntity<?> getPdfCW(@RequestParam("area") String param) throws Exception {
        try {
            if (param == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            CopyWriting cw = copyWritingService.getCwByArea(param);

            return new ResponseEntity<>(cw, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "获取协议失败，请稍后重试"));
        }
    }
}