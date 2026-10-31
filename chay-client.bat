@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist "bin\vn\dau\diemdanh\mang\StudentClient.class" (
    echo Đang biên dịch mã nguồn...
    powershell -NoProfile -Command "[System.IO.File]::WriteAllLines('sources.txt', (Get-ChildItem -Recurse -Filter *.java src | Resolve-Path -Relative), (New-Object System.Text.UTF8Encoding $false)); cmd /c 'javac -encoding UTF-8 -d bin -cp bin;lib/* @sources.txt'; Remove-Item sources.txt"
)
java -Dfile.encoding=UTF-8 -cp bin vn.dau.diemdanh.mang.StudentClient %*
pause
