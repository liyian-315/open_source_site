package com.sdu.open.source.site.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
@Slf4j
public class AsyncMailService {

    private final MailMsgService mailMsgService;

    public AsyncMailService(MailMsgService mailMsgService) {
        this.mailMsgService = mailMsgService;
    }

    /**
     * 异步发送验证码邮件
     * @param email 接收邮箱
     */
    @Async
    public void asyncSendVerificationCode(String email) {
        try {
            boolean sendSuccess = mailMsgService.mail(email);
            if (sendSuccess) {
                log.info("异步发送验证码成功，邮箱：{}", email);
            } else {
                log.error("异步发送验证码失败，邮箱：{}", email);
                // 可选：失败后重试机制（如重试1-2次，避免因网络波动导致的失败）
                 retrySend(email, 1);
            }
        } catch (MessagingException e) {
            log.error("异步发送邮件异常，邮箱：{}，异常信息：{}", email, e.getMessage(), e);
        } catch (Exception e) {
            log.error("异步发送验证码系统异常，邮箱：{}，异常信息：{}", email, e.getMessage(), e);
        }
    }

    /**
     * 可选：失败重试方法
     * @param email 接收邮箱
     * @param retryCount 已重试次数
     */
    private void retrySend(String email, int retryCount) throws MessagingException {
        if (retryCount > 2) {
            log.error("邮箱 {} 重试发送次数已达上限，停止重试", email);
            return;
        }
        log.info("邮箱 {} 第 {} 次重试发送验证码", email, retryCount);
        try {
            Thread.sleep(1000);
            mailMsgService.mail(email);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("重试线程中断，邮箱：{}", email);
        } catch (MessagingException e) {
            retrySend(email, retryCount + 1);
        }
    }
}
