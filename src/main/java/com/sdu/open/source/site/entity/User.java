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
    private String fullname;
    private String email2;
    private String phone;
    private String company;
    private String address;
    private String bankCardNumber;
    private String createTime;
    private String updateTime;
    private Boolean hasSignedPdf;
}