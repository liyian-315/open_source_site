package com.sdu.open.source.site.dto;

import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String email2;
    private String phone;
    private String company;
    private String address;
}