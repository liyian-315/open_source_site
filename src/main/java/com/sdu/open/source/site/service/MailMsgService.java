package com.sdu.open.source.site.service;

import com.sdu.open.source.site.utils.CodeGeneratorUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Duration;

/**
 * @Author: liyian
 * @Description: 发送邮箱业务
 * @CreateTime: 2025-10-09  17:46
 * @Version: 1.0
 */
@Component
public class MailMsgService {
    @Value("${spring.mail.username}")
    private String mailUsername;
    private final JavaMailSenderImpl mailSender;
    private final RedisTemplate<String,String> redisTemplate;
    private final EmailTemplateService emailTemplateService;

    public MailMsgService(JavaMailSenderImpl mailSender, RedisTemplate<String, String> redisTemplate, EmailTemplateService emailTemplateService) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
        this.emailTemplateService = emailTemplateService;
    }

    /**
     * 发送验证码邮件
     * @param email 目标邮箱
     * @return 是否发送成功
     */
    public boolean sendVerificationCode(String email) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String code = CodeGeneratorUtil.generateCode(6);
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // 使用模板服务生成内容
        helper.setText(emailTemplateService.getVerificationCodeTemplate(code), true);
        helper.setSubject("【东山社区】验证码");
        helper.setTo(email);
        helper.setFrom(mailUsername);

        // 存储验证码到Redis
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(60));
        mailSender.send(mimeMessage);
        return true;
    }

    /**
     * 发送密码重置邮件
     * @param email 目标邮箱
     * @param resetUrl 重置链接
     * @return 是否发送成功
     */
    public boolean sendResetPasswordEmail(String email, String resetUrl) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // 使用模板服务生成内容
        helper.setText(emailTemplateService.getResetPasswordTemplate(resetUrl), true);
        helper.setSubject("Reset your password");
        helper.setTo(email);
        helper.setFrom(mailUsername);

        mailSender.send(mimeMessage);
        return true;
    }
}
