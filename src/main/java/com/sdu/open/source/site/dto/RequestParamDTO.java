package com.sdu.open.source.site.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 请求参数体
 */
@Data
public class RequestParamDTO {
    /**
     * 个人信息
     */
    // 用户名：必填，用于定位用户
    @NotBlank(message = "用户名不能为空")
    private String username;

    // 备用邮箱：可选，但格式必须正确
    @Pattern(regexp = "^$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "备用邮箱格式不正确")
    private String email2;

    // 手机号：必填，11位有效手机号
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$",
            message = "请输入有效的11位手机号")
    private String phone;

    // 单位：可选，最大50字符
    @Size(max = 50, message = "单位名称不能超过50个字符")
    private String company;

    // 地址：可选，最大100字符
    @Size(max = 100, message = "地址不能超过100个字符")
    private String address;

    // 银行卡号：可选，13-19位数字
    @Pattern(regexp = "^$|^\\d{13,19}$",
            message = "银行卡号必须是13-19位数字")
    private String bankCardNumber;
}
