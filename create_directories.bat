@echo off
setlocal enabledelayedexpansion

REM Create all required directories
mkdir "D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\data\model"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Created: data\model
) else (
    echo ✗ Failed: data\model
)

mkdir "D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\data\network"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Created: data\network
) else (
    echo ✗ Failed: data\network
)

mkdir "D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\data\repository"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Created: data\repository
) else (
    echo ✗ Failed: data\repository
)

mkdir "D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\ui\home"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Created: ui\home
) else (
    echo ✗ Failed: ui\home
)

mkdir "D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\ui\navigation"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Created: ui\navigation
) else (
    echo ✗ Failed: ui\navigation
)

echo.
echo All directories created successfully!
