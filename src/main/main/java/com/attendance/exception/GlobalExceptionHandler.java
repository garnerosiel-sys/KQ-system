package com.attendance.exception;

import com.attendance.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 使用 {@code @RestControllerAdvice} 统一拦截 Controller 层抛出的所有异常，
 * 将异常转换为标准的 {@link Result} JSON 响应，避免堆栈信息直接暴露给前端。
 * </p>
 *
 * <h3>异常分类处理策略</h3>
 * <table border="1">
 *   <tr><th>异常类型</th><th>处理方式</th><th>返回内容</th></tr>
 *   <tr>
 *     <td>{@link BusinessException}</td>
 *     <td>捕获后取出其中的 message，包装为 Result</td>
 *     <td>{@code Result.error(e.getMessage())}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Exception}（未知运行时异常）</td>
 *     <td>记录完整堆栈日志，向前端返回模糊提示</td>
 *     <td>{@code Result.error("系统繁忙，请稍后重试")}</td>
 *   </tr>
 * </table>
 *
 * <h3>启用方式</h3>
 * <p>
 * 本项目使用 SSM 架构，有两种启用方式：
 * </p>
 * <ol>
 *   <li><b>XML 配置（当前方式）</b>：在 {@code spring-mvc.xml} 中配置
 *       {@code <context:component-scan base-package="com.attendance.exception"/>}，
 *       配合 {@code <mvc:annotation-driven/>} 自动注册。</li>
 *   <li><b>Java 配置</b>：{@link com.attendance.config.SpringConfig} 中
 *       {@code @ComponentScan} 需包含 {@code com.attendance.exception} 包路径。</li>
 * </ol>
 *
 * @author KQ-system
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常 {@link BusinessException}
     * <p>
     * 业务异常是开发者在 Service 层主动抛出的、可预期的异常。
     * 直接将其携带的消息返回给前端，状态码使用异常中定义的 code。
     * </p>
     *
     * @param e 业务异常
     * @return Result 对象，包含异常中的 message
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.warn("业务异常 [code={}]: {}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理未预期的系统异常 {@link Exception}
     * <p>
     * 兜底处理器，捕获所有未被 {@link #handleBusinessException} 拦截的异常。
     * 向前端返回统一的友好提示"系统繁忙，请稍后重试"，
     * 同时将完整堆栈信息记录在服务端日志中，方便排查。
     * </p>
     *
     * @param e 异常对象
     * @return Result 对象，code=500，message="系统繁忙，请稍后重试"
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 将完整堆栈打印到日志，方便运维排查
        log.error("系统未知异常: ", e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
