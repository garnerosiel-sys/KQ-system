@echo off
echo Initializing database for KQ System...

REM 检查MySQL服务是否正在运行
net start | findstr /i "MySQL"
if %ERRORLEVEL% neq 0 (
    echo MySQL service is not running. Please start MySQL first.
    pause
    exit /b 1
)

REM 尝试在常见位置查找MySQL
set MYSQL_PATH=
for /d %%i in ("C:\Program Files\MySQL\*") do (
    if exist "%%i\bin\mysql.exe" (
        set MYSQL_PATH=%%i\bin\mysql.exe
        goto :found
    )
)

if not defined MYSQL_PATH (
    echo MySQL executable not found in common locations.
    echo Please add MySQL to PATH or specify the full path.
    pause
    exit /b 1
)

:found
echo Found MySQL at: %MYSQL_PATH%

REM 执行初始化脚本
echo Creating database and tables...
"%MYSQL_PATH%" -u root -p < src\main\resources\init.sql

if %ERRORLEVEL% equ 0 (
    echo Database initialization completed successfully!
) else (
    echo Error occurred during initialization.
)

pause