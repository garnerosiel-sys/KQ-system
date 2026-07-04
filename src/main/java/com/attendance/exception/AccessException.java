package com.attendance.exception;

/**
 * 访问权限异常
 * <p>
 * 当用户尝试访问无权限的资源时抛出。
 * </p>
 *
 * @author KQ-system
 */
public class AccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccessException(String message) {
        super(message);
    }

    public AccessException(String message, Throwable cause) {
        super(message, cause);
    }
}