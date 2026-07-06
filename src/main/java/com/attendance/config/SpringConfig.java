package com.attendance.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Spring 根容器配置（替代 applicationContext.xml）
 * <p>
 * 管理 Service、Mapper、数据源、事务等中间层组件。
 * MVC 层组件（Controller / 拦截器 / 异常处理器）交给 {@link WebMvcConfig} 管理。
 * </p>
 *
 * @author KQ-system
 */
@Configuration
@ComponentScan(
        basePackages = "com.attendance",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION,
                        value = org.springframework.stereotype.Controller.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION,
                        value = org.springframework.web.bind.annotation.RestControllerAdvice.class)
        }
)
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
@EnableTransactionManagement
public class SpringConfig {

    @Value("${jdbc.driver}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.initialSize}")
    private int initialSize;

    @Value("${jdbc.minIdle}")
    private int minIdle;

    @Value("${jdbc.maxActive}")
    private int maxActive;

    @Value("${jdbc.maxWait}")
    private long maxWait;

    // ==================== Druid 数据源 ====================

    @Bean
    public DataSource dataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setInitialSize(initialSize);
        ds.setMinIdle(minIdle);
        ds.setMaxActive(maxActive);
        ds.setMaxWait(maxWait);
        ds.setValidationQuery("SELECT 1");
        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);
        return ds;
    }

    // ==================== MyBatis ====================

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(
            DataSource dataSource,
            @Value("${mybatis.typeAliasesPackage}") String typeAliasesPackage,
            @Value("${mybatis.mapperLocations}") String mapperLocations) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage(typeAliasesPackage);
        // 将 classpath:mapper/*.xml 解析为 Resource[] 并注入
        factory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources(mapperLocations));

        org.apache.ibatis.session.Configuration cfg = new org.apache.ibatis.session.Configuration();
        cfg.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(cfg);
        return factory;
    }

    /**
     * 必须 static。MapperScannerConfigurer 是 BeanDefinitionRegistryPostProcessor，
     * 若声明为非 static @Bean，会迫使 SpringConfig 在后置处理器阶段被提前实例化，
     * 导致 @Value 字段来不及注入（maxActive 等保持默认值 0）。
     */
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer scanner = new MapperScannerConfigurer();
        scanner.setBasePackage("com.attendance.mapper");
        scanner.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return scanner;
    }

    // ==================== 事务管理器 ====================

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
