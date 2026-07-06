package com.attendance.exception;

/**
 * 业务异常类
 * <p>
 * 用于在 Service 层抛出业务逻辑相关的异常，
 * 由 {@link GlobalExceptionHandler} 统一拦截并返回标准 JSON 格式。
 * </p>
 *
 * <pre>
 * 使用示例：
 *   throw new BusinessException("用户名或密码错误");
 *   throw new BusinessException(401, "Token 已过期");
 * </pre>
 *
 * @author KQ-system
 */
public class BusinessException extends RuntimeException {

    /** 异常状态码 */
    private int code;

    /**
     * 使用默认状态码 500 构造业务异常
     *
     * @param message 异常描述信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 使用自定义状态码构造业务异常
     *
     * @param code    业务状态码
     * @param message 异常描述信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用自定义状态码和原始异常构造业务异常
     *
     * @param code    业务状态码
     * @param message 异常描述信息
     * @param cause   原始异常
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
