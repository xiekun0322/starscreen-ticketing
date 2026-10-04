package com.starscreen.repository;

import com.starscreen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 【功能】用户数据访问层。
 *         继承 JpaRepository，自动获得 save / findById / findAll / deleteById 等基础方法。
 *
 * 【调用方】
 *          - UserService           注册 / 登录 / 绑定账号 / 重置密码
 *          - UserController        REST API
 *          - UserServiceTest       单元测试
 *
 * 【被调用】
 *          Spring Data JPA 运行时自动生成实现类，SQL 由方法名推导，
 *          底层通过 Hibernate 访问 MySQL 的 t_user 表。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 【功能】按用户名查询用户。
     * 【SQL】SELECT * FROM t_user WHERE username = ?
     * 【调用链】
     *   UserService.login(LoginRequest)
     *   → 用用户名查用户 → 校验密码
     * @param username 用户名
     * @return Optional 空表示用户不存在
     */
    Optional<User> findByUsername(String username);

    /**
     * 【功能】判断用户名是否已被占用。
     * 【SQL】SELECT COUNT(*) FROM t_user WHERE username = ?
     * 【调用链】
     *   UserService.register(RegisterRequest)         → 注册前检查用户名重复
     *   UserService.bindAccount(userId, req)          → 绑定账号前检查用户名重复
     * @param username 用户名
     * @return true 表示已存在
     */
    boolean existsByUsername(String username);

    /**
     * 【功能】按手机号查询用户。
     * 【SQL】SELECT * FROM t_user WHERE phone = ?
     * 【调用链】
     *   UserService.loginOrRegisterByPhone(phone)     → 手机号登录 / 自动注册
     *   UserService.resetPassword(phone, newPassword) → 重置密码前检查手机号已注册
     * @param phone 手机号
     * @return Optional 空表示该手机号未注册
     */
    Optional<User> findByPhone(String phone);
}