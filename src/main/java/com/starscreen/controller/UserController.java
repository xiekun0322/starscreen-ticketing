package com.starscreen.controller;

import com.starscreen.common.Result;
import com.starscreen.dto.*;
import com.starscreen.entity.User;
import com.starscreen.service.SmsService;
import com.starscreen.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SmsService smsService;

    /** 账号密码登录（仅限已绑定密码的用户） */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody @Valid LoginRequest req, HttpSession session) {
        User user = userService.login(req);
        writeSession(session, user);
        return Result.success(UserVO.from(user));
    }

    /** 短信验证码登录（首次注册的唯一入口） */
    @PostMapping("/login-sms")
    public Result<UserVO> loginBySms(@RequestBody @Valid SmsLoginRequest req, HttpSession session) {
        smsService.verifyCode(req.getPhone(), req.getCode());
        User user = userService.loginOrRegisterByPhone(req.getPhone());
        writeSession(session, user);
        return Result.success(UserVO.from(user));
    }

    /** 发送短信验证码 */
    @PostMapping("/send-sms")
    public Result<Void> sendSms(@RequestBody @Valid SendSmsRequest req) {
        smsService.sendCode(req.getPhone());
        return Result.success();
    }

    /**
     * ★ 绑定账号密码（已用手机号登录的用户）。
     * 【调用方】个人中心页面，登录后可访问。
     */
    @PostMapping("/bind-account")
    public Result<UserVO> bindAccount(@RequestBody @Valid BindAccountRequest req, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error(401, "请先登录");
        User user = userService.bindAccount(userId, req);
        return Result.success(UserVO.from(user));
    }

    /** 登出 */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    /** 当前登录用户 */
    @GetMapping("/current")
    public Result<UserVO> current(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error(401, "未登录");
        User user = userService.getById(userId);
        if (user == null) {
            session.invalidate();
            return Result.error(401, "登录已失效");
        }
        return Result.success(UserVO.from(user));
    }

    /** 保留旧接口，仅供测试/内部使用 */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody @Valid RegisterRequest req) {
        return Result.success(UserVO.from(userService.register(req)));
    }

    private void writeSession(HttpSession session, User user) {
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole() != null ? user.getRole() : "USER");
    }
    
    /**
     * 【功能】重置密码（忘记密码）。
     * 【调用链】login.html → 忘记密码弹窗 → POST /api/user/reset-password
     * 【流程】
     *   1. 校验手机验证码（SmsService）
     *   2. 更新密码（UserService）
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        // 先校验验证码
        smsService.verifyCode(req.getPhone(), req.getCode());
        // 再重置密码
        userService.resetPassword(req.getPhone(), req.getNewPassword());
        return Result.success();
    }
}