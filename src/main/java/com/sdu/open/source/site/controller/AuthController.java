package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.dto.ApiResponse;
import com.sdu.open.source.site.dto.JwtRequest;
import com.sdu.open.source.site.dto.JwtResponse;
import com.sdu.open.source.site.dto.RegisterRequest;
import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.security.JwtTokenUtil;
import com.sdu.open.source.site.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

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

    private final CopyWritingService copyWritingService;
    private final RedisTemplate<String, String> redisTemplate;
    private final AsyncMailService asyncMailService;

    public AuthController(CopyWritingService copyWritingService, RedisTemplate<String, String> redisTemplate, AsyncMailService asyncMailService) {
        this.copyWritingService = copyWritingService;
        this.redisTemplate = redisTemplate;
        this.asyncMailService = asyncMailService;
    }

    @Value("${spring.constant.site-url}")
    private String siteUrl;

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
            // 校验验证码
            String email = request.getEmail();
            String inputCode = request.getVerificationCode();
            if (StringUtils.isEmpty(inputCode)) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "请输入邮箱验证码"));
            }
            String storedCode = redisTemplate.opsForValue().get(email);
            if (storedCode == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "验证码已过期，请重新获取"));
            }
            if (!storedCode.equals(inputCode)) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "验证码不正确"));
            }
            redisTemplate.delete(email);
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
            user.setHasSignedPdf(false);

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

    @GetMapping("/public/getPdfCW")
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

    @GetMapping(value = "/public/sendEmail/{email}")
    public ResponseEntity<?> sendEmail(@PathVariable("email") String email) {
        try {
            if (!isValidEmail(email)) {
                log.warn("邮箱格式不正确，邮箱：{}", email);
                return ResponseEntity.ok(ApiResponse.error(400, "邮箱格式不正确"));
            }
            asyncMailService.asyncSendVerificationCode(email);
            log.info("验证码发送任务已提交，邮箱：{}", email);
            return ResponseEntity.ok(ApiResponse.success("验证码已开始发送，请查收邮件（若5分钟内未收到，可重新获取）"));

        } catch (Exception e) {
            log.error("验证码发送任务提交失败，邮箱：{}，异常信息：{}", email, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(500, "系统繁忙，请稍后重试"));
        }
    }

    // 忘记密码-发送重置邮件
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody RequestParamDTO param) {
        try {
            String email = param.getForgotEmail();
            String captchaCode = param.getCaptchaCode();

            // 验证邮箱格式
            if (!isValidEmail(email)) {
                return ResponseEntity.ok(ApiResponse.error(400, "邮箱格式不正确"));
            }

            // todo 验证验证码（从Redis获取存储的验证码）
            String storedCaptcha = redisTemplate.opsForValue().get(email);
            if (storedCaptcha == null) {
                return ResponseEntity.ok(ApiResponse.error(400, "验证码已过期，请重新获取"));
            }
            if (!storedCaptcha.equals(captchaCode)) {
                return ResponseEntity.ok(ApiResponse.error(400, "验证码不正确"));
            }
            redisTemplate.delete(email);

            // 查找用户
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.success("若邮箱已注册，重置链接将发送至该邮箱"));
            }

            // 生成JWT作为重置令牌（1小时）
            String resetToken = jwtTokenUtil.generateResetToken(user.getUsername());

            // 构造重置链接（需替换为实际前端页面地址）
            String resetUrl = siteUrl + "/reset-password?token=" + resetToken;

            // 异步发送重置邮件
            asyncMailService.asyncSendResetPasswordEmail(email, resetUrl);

            return ResponseEntity.ok(ApiResponse.success("密码重置邮件已发送，请注意查收"));
        } catch (Exception e) {
            log.error("忘记密码流程异常", e);
            return ResponseEntity.ok(ApiResponse.error(500, "系统繁忙，请稍后重试"));
        }
    }

    // 验证重置令牌并重置密码
    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam("token") String token,
            @RequestBody RequestParamDTO param) {
        try {
            String newPassword = param.getNewPassword();
            String confirmPassword = param.getConfirmPassword();
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.ok(ApiResponse.error(400, "两次输入的密码不一致"));
            }
            // 验证JWT令牌
            String username = jwtTokenUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtTokenUtil.validateToken(token, userDetails) || !jwtTokenUtil.isResetToken(token)) {
                return ResponseEntity.ok(ApiResponse.error(400, "令牌无效或已过期"));
            }

            // 获取用户名并更新密码
            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
            }
            user.setPassword(newPassword);
            userService.updatePassword(user);

            return ResponseEntity.ok(ApiResponse.success("密码重置成功，请使用新密码登录"));
        } catch (Exception e) {
            log.error("重置密码流程异常", e);
            return ResponseEntity.ok(ApiResponse.error(500, "系统繁忙，请稍后重试"));
        }
    }
    private boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }
}