package com.sdu.open.source.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWT响应DTO
 */
@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String username;
    private String role;
}