package com.attendance.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * H2 嵌入式数据库配置（仅在 h2 profile 激活时生效）
 * <p>
 * 另一台电脑无需安装 MySQL，启动时自动建库建表。
 * 使用方式：mvn jetty:run -Dspring.profiles.active=h2
 * </p>
 *
 * @author KQ-system
 */
@Configuration
@Profile("h2")
@PropertySource(value = "classpath:application-h2.properties", encoding = "UTF-8")
public class H2Config {

    private static final Logger log = LoggerFactory.getLogger(H2Config.class);

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

    /**
     * H2 数据源，@Primary 确保注入优先级高于 MySQL 数据源。
     * 启动时自动执行 init-h2.sql 建表并插入种子数据。
     */
    @Bean
    @Primary
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

        log.info("H2 数据源初始化完成，数据库文件: ./data/kqsystem.mv.db");

        // 检查是否需要初始化表结构
        try (Connection conn = ds.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1 FROM department");
                log.info("H2 表结构已存在，跳过初始化");
            } catch (Exception e) {
                // 表不存在，执行建表脚本
                log.info("H2 表不存在，执行 init-h2.sql 建表...");
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("init-h2.sql"));
                populator.populate(conn);
                log.info("H2 数据库初始化完成");
            }
        } catch (Exception e) {
            log.error("H2 数据库初始化失败", e);
        }

        return ds;
    }
}
