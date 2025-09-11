package com.sdu.open.source.site.entity;

import lombok.Data;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;
    private Boolean enabled;
    private String createTime;
    private String updateTime;
}