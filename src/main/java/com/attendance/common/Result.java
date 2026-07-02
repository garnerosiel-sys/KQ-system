package com.attendance.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一 API 响应结果封装类
 * <p>
 * 所有 Controller 接口和异常处理器均使用此类作为返回值，
 * 确保前端接收到的 JSON 结构完全一致。
 * </p>
 *
 * <h3>标准返回格式</h3>
 * <pre>
 *  成功: { "code": 200, "message": "操作成功", "data": {...} }
 *  失败: { "code": 500, "message": "错误描述", "data": null }
 * </pre>
 *
 * <h3>使用示例</h3>
 * <pre>
 *   // 成功返回（无数据）
 *   return Result.success();
 *
 *   // 成功返回（带数据）
 *   return Result.success(userList);
 *
 *   // 业务异常
 *   return Result.error("用户名或密码错误");
 *
 *   // 自定义状态码
 *   return Result.error(401, "Token 已过期");
 * </pre>
 *
 * @author KQ-system
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {

    /** 状态码（200 = 成功，其他 = 失败） */
    private int code;

    /** 提示消息 */
    private String message;

    /** 响应数据（可为空） */
    private Object data;

    // ==================== 构造方法 ====================

    public Result() {
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 操作成功（无返回数据）
     */
    public static Result success() {
        return new Result(200, "操作成功", null);
    }

    /**
     * 操作成功（带返回数据）
     *
     * @param data 响应数据
     */
    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    /**
     * 操作成功（自定义消息 + 数据）
     *
     * @param message 成功提示
     * @param data    响应数据
     */
    public static Result success(String message, Object data) {
        return new Result(200, message, data);
    }

    /**
     * 操作失败（默认状态码 500）
     *
     * @param message 错误描述
     */
    public static Result error(String message) {
        return new Result(500, message, null);
    }

    /**
     * 操作失败（自定义状态码）
     *
     * @param code    业务错误码
     * @param message 错误描述
     */
    public static Result error(int code, String message) {
        return new Result(code, message, null);
    }

    // ==================== Getter / Setter ====================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{code=" + code + ", message='" + message + "', data=" + data + "}";
    }
}
