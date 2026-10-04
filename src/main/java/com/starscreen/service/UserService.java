package com.starscreen.service;

import com.starscreen.common.BusinessException;
import com.starscreen.dto.BindAccountRequest;
import com.starscreen.dto.LoginRequest;
import com.starscreen.entity.User;
import com.starscreen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final UserRepository userRepository;

    /**
     * 【功能】手机号登录或自动注册。
     * 【规则】
     *   - 手机号存在 → 直接返回该用户
     *   - 手机号不存在 → 自动创建新用户（用户名 = "用户" + 手机后4位，无密码）
     * 【说明】新用户首次注册必须用手机号，这是唯一入口。
     */
    @Transactional
    public User loginOrRegisterByPhone(String phone) {
        return userRepository.findByPhone(phone).orElseGet(() -> {
            User user = new User();
            user.setUsername("用户" + phone.substring(7));
            user.setPassword("");    // 无密码，只能用手机号登录
            user.setPhone(phone);
            user.setCreateTime(LocalDateTime.now().format(DTF));
            user.setRole("USER");
            User saved = userRepository.save(user);
            log.info("手机号自动注册：id={}, phone={}", saved.getId(), phone);
            return saved;
        });
    }

    /**
     * 【功能】账号密码登录。
     * 【前提】用户必须已经绑定过密码（password 非空）。
     * 【异常】未绑定密码 → "该账号未绑定密码，请使用手机号验证码登录"
     */
    public User login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 未绑定密码的账号 → 提示用手机号登录
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException("该账号未绑定密码，请使用手机号验证码登录");
        }

        if (!PASSWORD_ENCODER.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        log.info("账号密码登录成功：id={}, username={}", user.getId(), user.getUsername());
        return user;
    }

    /**
     * 【功能】绑定账号密码（已用手机号登录的用户，在个人中心绑定）。
     * 【调用链】UserController.bindAccount(userId, req)
     * 【异常】
     *   - 用户名已被占用
     *   - 已绑定过密码（避免重复绑定；如需改密走"修改密码"接口）
     *   - 用户不存在
     */
    @Transactional
    public User bindAccount(Long userId, BindAccountRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            throw new BusinessException("该账号已绑定密码，请直接使用账号密码登录");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException("用户名已被占用");
        }

        user.setUsername(req.getUsername());
        user.setPassword(PASSWORD_ENCODER.encode(req.getPassword()));
        user = userRepository.save(user);

        log.info("用户绑定账号密码：id={}, username={}", userId, user.getUsername());
        return user;
    }

    // ==================== 兼容旧接口（保留，供已有代码调用） ====================

    /**
     * 【保留】传统注册（用户名 + 密码）。
     * 【说明】不在 UI 上暴露，仍保留给测试或内部使用。
     */
    @Transactional
    public User register(com.starscreen.dto.RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (req.getPhone() != null && !req.getPhone().isEmpty()
                && userRepository.findByPhone(req.getPhone()).isPresent()) {
            throw new BusinessException("手机号已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(PASSWORD_ENCODER.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setCreateTime(LocalDateTime.now().format(DTF));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    /**
     * 【功能】重置密码（忘记密码场景）。
     * 【前提】手机号必须已注册。
     * 【调用链】
     *   UserController.resetPassword(@Valid ResetPasswordRequest)
     *   → POST /api/user/reset-password
     *   → UserService.resetPassword(phone, code, newPassword)
     *   → smsService.verifyCode(phone, code)   // Controller 里先校验
     *   → 更新 password 为 BCrypt hash
     * 【异常】
     *   - "该手机号未注册"
     *   - "验证码错误/已过期"（由 SmsService 抛出）
     */
    @Transactional
    public User resetPassword(String phone, String newPassword) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessException("该手机号未注册"));

        user.setPassword(PASSWORD_ENCODER.encode(newPassword));
        user = userRepository.save(user);

        log.info("用户重置密码：id={}, phone={}", user.getId(), phone);
        return user;
    }
}
