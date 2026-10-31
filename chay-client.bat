@echo off
setlocal EnableDelayedExpansion
chcp 65001 >nul
cd /d "%~dp0"
if not exist "bin\vn\dau\diemdanh\mang\StudentClient.class" (
    echo Đang biên dịch mã nguồn...
    powershell -NoProfile -Command "[System.IO.File]::WriteAllLines('sources.txt', (Get-ChildItem -Recurse -Filter *.java src | Resolve-Path -Relative), (New-Object System.Text.UTF8Encoding $false)); cmd /c 'javac -encoding UTF-8 -d bin -cp bin;lib/* @sources.txt'; Remove-Item sources.txt"
)
if "%~4"=="" (
    echo =======================================================
    echo GỬI ĐIỂM DANH SINH VIÊN QUA MẠNG NỘI BỘ
    echo =======================================================
    set "HOST=127.0.0.1"
    set "PORT=8888"
    set /p "IN_HOST=1. Nhập IP Máy chủ (mặc định 127.0.0.1): "
    if not "!IN_HOST!"=="" set "HOST=!IN_HOST!"
    set /p "IN_PORT=2. Nhập Cổng (mặc định 8888): "
    if not "!IN_PORT!"=="" set "PORT=!IN_PORT!"
    set /p "PHIEN=3. Nhập Mã phiên (ví dụ P01): "
    set /p "MASV=4. Nhập Mã sinh viên (ví dụ 22CNTT01): "
    echo.
    java -cp bin vn.dau.diemdanh.mang.StudentClient !HOST! !PORT! !PHIEN! !MASV!
) else (
    java -cp bin vn.dau.diemdanh.mang.StudentClient %1 %2 %3 %4
)
echo.
pause
