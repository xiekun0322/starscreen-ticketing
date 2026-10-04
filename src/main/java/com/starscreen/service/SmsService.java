package com.starscreen.service;

import com.starscreen.common.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟短信验证码服务
 * 演示环境：验证码打印到 Console
 * 生产环境：接入阿里云短信 / 腾讯云短信 + Redis
 */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    /** 验证码有效期：5 分钟 */
    private static final long CODE_TTL_MS = 5 * 60 * 1000;

    /** 内存存储：手机号 -> [验证码, 过期时间] */
    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();

    private final Random random = new Random();

    /**
     * 发送验证码（模拟）
     */
    public void sendCode(String phone) {
        // 生成 6 位随机码（000000 ~ 999999）
        String code = String.format("%06d", random.nextInt(1000000));

        // 存储
        codeStore.put(phone, new CodeEntry(code, System.currentTimeMillis() + CODE_TTL_MS));

        // ★ 模拟发送：打印到 Eclipse Console
        log.info("========================================");
        log.info("★ 模拟短信发送");
        log.info("  手机号：{}", phone);
        log.info("  验证码：{}", code);
        log.info("  有效期：5 分钟");
        log.info("========================================");

        // 生产环境代码示例：
        // smsClient.send(phone, code);
    }

    /**
     * 校验验证码
     */
    public void verifyCode(String phone, String code) {
        CodeEntry entry = codeStore.get(phone);
        if (entry == null) {
            throw new BusinessException("验证码未发送或已过期");
        }
        if (entry.expireAt < System.currentTimeMillis()) {
            codeStore.remove(phone);
            throw new BusinessException("验证码已过期");
        }
        if (!entry.code.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        // 校验成功后删除（一次性使用）
        codeStore.remove(phone);
    }

    /** 存储结构 */
    private static class CodeEntry {
        final String code;
        final long expireAt;
        CodeEntry(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }
}
