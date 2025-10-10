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

    public MailMsgService(JavaMailSenderImpl mailSender, RedisTemplate<String, String> redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    public boolean mail(String email) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String code = CodeGeneratorUtil.generateCode(6);
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setText(String.format(
                "<p style='font-size: 16px;'>您的验证码为：<strong style='color: #1a73e8; font-size: 18px;'>%s</strong></p>" +
                        "<p style='font-size: 14px; color: #666;'>该验证码有效期为60分钟，请尽快使用</p>",
                code
        ), true);
        helper.setSubject("【东山社区】验证码");
        helper.setTo(email);
        helper.setFrom(mailUsername);
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(60));
        mailSender.send(mimeMessage);
        return true;
    }
}
