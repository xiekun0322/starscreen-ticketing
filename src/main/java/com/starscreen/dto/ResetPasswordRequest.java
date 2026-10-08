package com.starscreen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 【功能】重置密码请求。
 *         用户在登录页点「忘记密码？」→ 手机验证码校验通过后提交。
 *
 * 【调用链】
 *   login.html → 忘记密码弹窗 → POST /api/user/reset-password
 *   → UserController.resetPassword(@Valid ResetPasswordRequest)
 *   → SmsService.verifyCode(phone, code)    // 校验验证码
 *   → UserService.resetPassword(phone, newPassword)
 */
@Data
public class ResetPasswordRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是 6 位数字")
    private String code;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度 8~20 位")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*._-])[A-Za-z\\d!@#$%^&*._-]{8,20}$",
        message = "密码必须包含大写字母、小写字母、数字和特殊字符，长度 8~20 位"
    )
    private String newPassword;
}
