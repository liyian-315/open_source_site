package com.sdu.open.source.site.dto;

import com.sdu.open.source.site.entity.CopyWriting;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 请求参数体
 */
@Data
public class RequestParamDTO {
    /**
     * 个人信息
     */
    private String username;
    private String fullname;
    private String role;
    private Boolean hasSignedPdf;
    // 备用邮箱：可选，但格式必须正确
    @Email(message = "备用邮箱格式不正确")
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

    private String giteeName;
    /**
     * task相关
     */
    private Long taskId;
    private Long taskUserId;
    private String taskName;
    private String collectionUser;
    private Integer taskStatus;
    private Integer recogStatus;
    /**
     * project相关
     */
    private Long projectId;  // 项目ID
    private String name;
    private String description;
    private String gitRepo;
    private String projectIntro;
    private List<Long> tagIds;         // 标签Id
    private List<CopyWriting> cwList; // 文案列表
    // 文案类型（区分PROJECT_DISPLAY/LEARNING_MATERIAL，参考CopyWritingAreas枚举）
    private String cwType;

    /*
     * 分页参数
     */
    private Integer pageNum;
    private Integer pageSize;

    // 忘记密码新增字段
    @Email(message = "邮箱格式不正确")
    private String forgotEmail;
    private String captchaCode;
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}