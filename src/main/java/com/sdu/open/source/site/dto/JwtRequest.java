package com.sdu.open.source.site.dto;

import lombok.Data;

/**
 * JWT请求DTO
 */
@Data
public class JwtRequest {
    private String username;
    private String password;
}