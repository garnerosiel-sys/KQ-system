package com.attendance.config;

import com.attendance.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 子容器配置（替代 spring-mvc.xml）
 * <p>
 * 管理 Controller、拦截器、全局异常处理器等 Web 层组件。
 * {@code @EnableWebMvc} = {@code <mvc:annotation-driven/>}。
 * </p>
 *
 * <h3>包扫描</h3>
 * <p>
 * 显式声明三个包路径，确保全局异常处理器和拦截器被 Spring MVC 子容器发现：
 * </p>
 * <ul>
 *   <li>{@code com.attendance.controller}  — Controller</li>
 *   <li>{@code com.attendance.exception}   — @RestControllerAdvice</li>
 *   <li>{@code com.attendance.interceptor} — LoginInterceptor</li>
 * </ul>
 *
 * @author KQ-system
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.attendance.controller",
        "com.attendance.exception",
        "com.attendance.interceptor"
})
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**")
                .addResourceLocations("/js/")
                .setCachePeriod(0);
        registry.addResourceHandler("/css/**")
                .addResourceLocations("/css/")
                .setCachePeriod(0);
        registry.addResourceHandler("/api/**")
                .addResourceLocations("/api/");
        registry.addResourceHandler("/*.html")
                .addResourceLocations("/")
                .setCachePeriod(0);
        registry.addResourceHandler("/*.jsp")
                .addResourceLocations("/")
                .setCachePeriod(0);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login", "/api/register");
    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
