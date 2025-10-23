package com.sdu.open.source.site.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaUtil {
    // 验证码字符集（数字+大小写字母）
    private static final String CHAR_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // 验证码长度
    private static final int CAPTCHA_LENGTH = 4;
    // 图片宽度
    private static final int WIDTH = 100;
    // 图片高度
    private static final int HEIGHT = 40;

    /**
     * 生成随机验证码（4位）
     */
    public static String generateCaptcha() {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
        }
        return captcha.toString();
    }

    /**
     * 生成验证码图片（带干扰线和噪点）
     */
    public static BufferedImage generateCaptchaImage(String captcha) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Random random = new Random();

        // 1. 绘制背景
        g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 2. 绘制验证码字符
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawString(String.valueOf(captcha.charAt(i)), 20 + i * 20, 30);
        }

        // 3. 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        // 4. 绘制噪点
        for (int i = 0; i < 30; i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.fillRect(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2, 2);
        }

        g.dispose();
        return image;
    }
}