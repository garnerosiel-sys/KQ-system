@echo off
chcp 65001 >nul
title 考勤管理系统 - KQ-system

echo ========================================
echo   考勤管理系统 KQ-system (H2 模式)
echo ========================================
echo.

:: 检查 Java 环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Java 环境！
    echo 请安装 Java 11 或更高版本：https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo [信息] Java 环境已就绪
echo.

:: 检查是否需要首次下载 Maven Wrapper
if not exist "mvnw.cmd" (
    echo [错误] 未找到 mvnw.cmd，请确保在项目根目录运行此脚本
    pause
    exit /b 1
)

echo [信息] 正在启动应用（H2 嵌入式数据库模式）...
echo [信息] 数据库文件将保存在 .\data\ 目录
echo [信息] 首次启动会自动下载依赖，请耐心等待...
echo.

:: 启动应用
call mvnw.cmd jetty:run -Dspring.profiles.active=h2

pause
