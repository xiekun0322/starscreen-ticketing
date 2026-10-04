package com.starscreen.common;

/**
 * 【功能】业务异常。
 *         用于表达"业务规则不满足"的情况，例如：
 *         座位已被选走、订单不存在、无权操作他人订单、用户名已存在等。
 *
 *         与系统异常区分：
 *         - BusinessException：预期内错误，GlobalExceptionHandler 会转成可读 JSON，不打印堆栈
 *         - 其它 Exception  ：非预期错误，GlobalExceptionHandler 会打完整堆栈，返回通用错误
 *
 * 【继承关系】RuntimeException（非受检异常）
 *          → Service 层可以直接 throw，不需要在方法签名上声明 throws
 *          → @Transactional 遇到 RuntimeException 会自动回滚
 *
 * 【调用方】
 *          - 所有 Service 层：抛出业务错误
 *            · OrderService.createOrder()  —— 座位不存在 / 已被选走
 *            · OrderService.pay()          —— 订单已支付 / 状态不允许支付 / 无权操作
 *            · OrderService.cancel()       —— 已支付不可取消 / 无权操作
 *            · UserService.register()      —— 用户名已存在
 *            · UserService.login()         —— 用户名或密码错误
 *            · MovieService                —— 电影不存在 / 电影名不能为空
 *            · ScheduleService             —— 场次不存在 / 参数校验
 *
 * 【被调用】
 *          GlobalExceptionHandler.handleBusiness(BusinessException e)
 *          —— 捕获后返回 Result.error(code, message)，HTTP 状态仍为 200
 */
public class BusinessException extends RuntimeException {

    /** 业务错误码：默认 500；常见值有 400（参数）、401（未登录）、403（越权）、404（不存在） */
    private final Integer code;

    /**
     * 【功能】默认构造：错误码 = 500。
     * 【调用链】Service → new BusinessException("座位已被选走") → GlobalExceptionHandler
     * @param message 面向用户的错误提示
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 【功能】指定错误码构造。
     * 【调用链】Service → new BusinessException(404, "订单不存在") → GlobalExceptionHandler
     * @param code    业务错误码
     * @param message 面向用户的错误提示
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 【功能】Getter：GlobalExceptionHandler 读取此 code 构造 Result。
     */
    public Integer getCode() {
        return code;
    }
}
