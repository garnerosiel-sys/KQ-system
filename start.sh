#!/bin/bash
# ============================================
# 考勤管理系统 KQ-system 启动脚本 (H2 模式)
# 适用于 macOS / Linux
# ============================================

echo "========================================"
echo "  考勤管理系统 KQ-system (H2 模式)"
echo "========================================"
echo ""

# 检查 Java 环境
if ! command -v java &> /dev/null; then
    echo "[错误] 未检测到 Java 环境！"
    echo "请安装 Java 11 或更高版本：https://adoptium.net/"
    exit 1
fi

echo "[信息] Java 环境已就绪"
echo ""

# 检查 mvnw
if [ ! -f "mvnw" ]; then
    echo "[错误] 未找到 mvnw，请确保在项目根目录运行此脚本"
    exit 1
fi

chmod +x mvnw 2>/dev/null

echo "[信息] 正在启动应用（H2 嵌入式数据库模式）..."
echo "[信息] 数据库文件将保存在 ./data/ 目录"
echo "[信息] 首次启动会自动下载依赖，请耐心等待..."
echo ""

# 启动应用
./mvnw jetty:run -Dspring.profiles.active=h2
