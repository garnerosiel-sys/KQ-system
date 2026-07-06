package com.attendance.test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据库连接测试类
 * <p>
 * 用于验证 application.properties 中的数据库配置是否正确。
 * 运行方式：在 IDE 中直接运行 main 方法，或使用 mvn test。
 * </p>
 *
 * @author KQ-system
 */
public class DatabaseConnectionTest {

    /** 数据库驱动类 */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /** 数据库连接地址 */
    private static final String URL = "jdbc:mysql://localhost:3306/attendance_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";

    /** 数据库用户名 */
    private static final String USERNAME = "root";

    /** 数据库密码 */
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        Connection connection = null;

        System.out.println("========== 考勤管理系统 - 数据库连接测试 ==========");
        System.out.println("连接地址: " + URL);
        System.out.println("用户名: " + USERNAME);
        System.out.println("--------------------------------------------------");

        try {
            // 1. 加载驱动
            Class.forName(DRIVER);
            System.out.println("[√] MySQL 驱动加载成功");

            // 2. 获取连接
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("[√] 数据库连接成功！");

            // 3. 验证数据库信息
            String catalog = connection.getCatalog();
            System.out.println("[√] 当前数据库: " + catalog);

            System.out.println("==================================================");
            System.out.println("测试通过！数据库 KQXT 可正常访问。");

        } catch (ClassNotFoundException e) {
            System.err.println("[×] MySQL 驱动加载失败: " + e.getMessage());
            System.err.println("    请检查 pom.xml 中 mysql-connector-java 依赖是否存在");
        } catch (java.sql.SQLException e) {
            System.err.println("[×] 数据库连接失败: " + e.getMessage());
            System.err.println("    请检查：");
            System.err.println("    1. MySQL 服务是否已启动");
            System.err.println("    2. 数据库 KQXT 是否已创建");
            System.err.println("    3. 用户名和密码是否正确");
            System.err.println("    4. 端口 3306 是否可访问");
        } finally {
            // 关闭连接
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("[√] 连接已关闭");
                } catch (java.sql.SQLException e) {
                    System.err.println("[×] 关闭连接失败: " + e.getMessage());
                }
            }
        }
    }
}
