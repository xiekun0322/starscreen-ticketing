package com.starscreen.dto;

import com.starscreen.entity.User;
import lombok.Data;

/**
 * 【功能】用户展示对象（VO）。
 *         专门用于把 User 实体转成前端可安全展示的结构，
 *         【不包含 password 字段】。
 *
 * 【调用方】
 *          - UserController.register() 返回注册结果
 *          - UserController.login()    返回登录结果（并把 id 写入 session）
 *          - UserController.current()  返回当前登录用户信息
 *
 * 【被调用】
 *          UserVO.from(user) 静态工厂方法把 User → UserVO。
 *
 * 【安全价值】
 *          如果直接返回 User 实体，Jackson 会把 password 一并序列化：
 *            { "id": 1, "username": "alice", "password": "123456" }
 *          前端 F12 就能看到明文密码。
 *          UserVO 只暴露 id / username / phone / createTime，杜绝泄露。
 *
 * 【生产改进点】
 *          - 返回手机号时可脱敏：138****8000
 *          - 加 avatar、role 字段（配合管理员角色）
 */
@Data
public class UserVO {

    /** 用户 ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 手机号（可选） */
    private String phone;

    /** 注册时间 */
    private String createTime;

    /**
     * 【功能】User → UserVO 的静态工厂。
     * 【调用链】
     *   UserController.login()      → UserVO.from(user) → Result.success(vo)
     *   UserController.register()   → UserVO.from(user)
     *   UserController.current()    → UserVO.from(userService.getById(userId))
     * 【空值处理】user 为 null 时返回 null，避免 NPE。
     * @param user JPA 实体
     * @return 只含安全字段的 VO
     */
    public static UserVO from(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setCreateTime(user.getCreateTime());
        return vo;   // 注意：不设置 password
    }
}