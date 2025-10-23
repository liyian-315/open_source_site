package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/public/captcha")
public class CaptchaController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 生成验证码图片（存储到 Redis，有效期 5 分钟）
     */
    @GetMapping(value = "/{email}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void getCaptcha(@PathVariable String email, HttpServletResponse response) throws IOException {
        String captcha = CaptchaUtil.generateCaptcha();
        BufferedImage image = CaptchaUtil.generateCaptchaImage(captcha);
        redisTemplate.opsForValue().set(email, captcha, 5, TimeUnit.MINUTES);
        ImageIO.write(image, "jpg", response.getOutputStream());
    }
}