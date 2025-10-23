package com.sdu.open.source.site.service;

import org.springframework.stereotype.Service;

/**
 * 邮件模板服务，管理各类邮件模板
 */
@Service
public class EmailTemplateService {

    /**
     * 生成验证码邮件模板
     * @param code 验证码
     * @return HTML 模板内容
     */
    public String getVerificationCodeTemplate(String code) {
        return String.format(
                "<p style='font-size: 16px;'>您的验证码为：<strong style='color: #1a73e8; font-size: 18px;'>%s</strong></p>" +
                        "<p style='font-size: 14px; color: #666;'>该验证码有效期为60分钟，请尽快使用</p>",
                code
        );
    }

    /**
     * 生成密码重置邮件模板
     * @param resetUrl 重置密码链接
     * @return HTML 模板内容
     */
    public String getResetPasswordTemplate(String resetUrl) {
        return String.format(
                "<h2>重置您的密码</h2>" +
                        "<p>我们注意到您需要重置密码，给您带来不便，敬请谅解！</p>" +
                        "<p>别担心，您可以通过以下按钮重置密码：</p>" +
                        "<p style='margin: 20px 0; text-align: center;'>" +
                        "   <a href='%s' style='display: inline-block; padding: 12px 30px; background-color: #28a745; color: #fff; text-decoration: none; border-radius: 4px; font-size: 16px;'>" +
                        "       重置密码" +
                        "   </a>" +
                        "</p>" +
                        "<p>此链接1小时内有效，逾期将自动失效。<a href='/api/auth/forgot-password' style='color: #0366d6;'>点击此处可重新获取密码重置链接。</a></p>" +
                        "<p>感谢您的使用<br>东山社区</p>",
                resetUrl
        );
    }
}