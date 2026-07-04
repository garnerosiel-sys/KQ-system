package com.attendance.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * Servlet 容器初始化类（替代 web.xml）
 * <p>
 * 基于 Servlet 3.0+ SPI，Tomcat 启动时自动发现并加载，
 * 无需 web.xml 即可初始化 Spring 双容器。
 * </p>
 *
 * <h3>双容器架构</h3>
 * <table border="1">
 *   <tr><th>容器</th><th>配置类</th><th>管理范围</th></tr>
 *   <tr><td>根容器</td><td>{@link SpringConfig}</td><td>Service、Mapper、数据源、事务</td></tr>
 *   <tr><td>Servlet 子容器</td><td>{@link WebMvcConfig}</td><td>Controller、拦截器、异常处理器</td></tr>
 * </table>
 *
 * @author KQ-system
 */
public class KqSystemInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{SpringConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /** 注册 Servlet Filter，等价于 web.xml 中的 CharacterEncodingFilter */
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return new Filter[]{encodingFilter};
    }
}
