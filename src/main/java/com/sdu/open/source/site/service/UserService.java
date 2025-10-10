package com.sdu.open.source.site.service;

import com.sdu.open.source.site.dto.RequestParamDTO;
import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用户服务类
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    /**
     * 创建新用户
     *
     * @param user 用户对象
     * @return 创建的用户对象
     */
    public User createUser(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认角色
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        // 设置账号状态
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        if (user.getHasSignedPdf() == null) {
            user.setHasSignedPdf(false);
        }

        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now.format(formatter));
        user.setUpdateTime(now.format(formatter));

        userDao.insert(user);
        return user;
    }

    /**
     * 验证用户凭据
     *
     * @param username 用户名
     * @param password 密码
     * @return 验证是否成功
     */
    public boolean validateCredentials(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    public void updateUserByParam(RequestParamDTO param) {
        User user = new User();
        user.setUsername(param.getUsername());
        if (param.getEmail2() != null) {
            user.setEmail2(param.getEmail2());
        }
        if (param.getPhone() != null) {
            user.setPhone(param.getPhone());
        }
        if (param.getCompany() != null) {
            user.setCompany(param.getCompany());
        }
        if (param.getAddress() != null) {
            user.setAddress(param.getAddress());
        }
        if (param.getBankCardNumber() != null) {
            user.setBankCardNumber(param.getBankCardNumber());
        }
        if (param.getGiteeName() != null) {
            user.setGiteeName(param.getGiteeName());
        }
        user.setUpdateTime(LocalDateTime.now().format(formatter));
        userDao.update(user);
    }
}