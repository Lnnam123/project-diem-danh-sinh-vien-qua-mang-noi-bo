param (
    [int]$Times = 1
)

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ErrorActionPreference = 'Continue'

$ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot '..')
Set-Location $ProjectRoot

$DocsDir = Join-Path $ProjectRoot 'docs'
if (-not (Test-Path $DocsDir)) {
    New-Item -ItemType Directory -Path $DocsDir -Force | Out-Null
}
$LogFile = Join-Path $DocsDir 'ket-qua-kiem-thu.txt'

$OutputLines = [System.Collections.Generic.List[string]]::new()

function Write-Log {
    param ([string]$Message = '')
    Write-Host $Message
    $OutputLines.Add($Message)
}

Write-Log '================================================================================'
Write-Log 'KIỂM THỬ TỰ ĐỘNG - ĐỀ TÀI 43: ĐIỂM DANH LỚP HỌC QUA MẠNG NỘI BỘ'
Write-Log ('Thời gian kiểm thử: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'))
Write-Log 'Môi trường: Java JDK 21 | JUnit 5 Platform Console Standalone'
Write-Log '================================================================================'

# 1. Xoá bin cũ
Write-Log '[1/3] Xoá thư mục bin/ cũ và khởi tạo lại...'
$BinDir = Join-Path $ProjectRoot 'bin'
if (Test-Path $BinDir) {
    Remove-Item -Recurse -Force $BinDir
}
New-Item -ItemType Directory -Path $BinDir -Force | Out-Null

# 2. Quét và biên dịch
Write-Log '[2/3] Quét và biên dịch toàn bộ src/ và test/ bằng javac (Java 21, UTF-8)...'
$SourcesFile = Join-Path $ProjectRoot 'sources.txt'
$JavaFiles = Get-ChildItem -Path (Join-Path $ProjectRoot 'src'), (Join-Path $ProjectRoot 'test') -Recurse -Filter *.java -ErrorAction SilentlyContinue

if ($null -eq $JavaFiles -or $JavaFiles.Count -eq 0) {
    Write-Log '[LỖI] Không tìm thấy file mã nguồn .java nào trong src/ hoặc test/!'
    Write-Log '================================================================================'
    Write-Log 'HỎNG'
    Write-Log '================================================================================'
    $OutputLines | Out-File -FilePath $LogFile -Encoding utf8
    exit 1
}

$JavaFiles | ForEach-Object { Resolve-Path -Relative $_.FullName } | Out-File -FilePath $SourcesFile -Encoding ascii

$JavacArgs = @(
    '-encoding', 'UTF-8',
    '-cp', 'bin;lib/*',
    '-d', 'bin',
    '@sources.txt'
)

$CompileProcess = Start-Process -FilePath 'javac' -ArgumentList $JavacArgs -NoNewWindow -PassThru -Wait -RedirectStandardError ('C:\Users\Nam\AppData\Local\Temp\javac_err.txt') -RedirectStandardOutput ('C:\Users\Nam\AppData\Local\Temp\javac_out.txt')

$CompileOutput = ''
if (Test-Path 'C:\Users\Nam\AppData\Local\Temp\javac_out.txt') {
    $CompileOutput += (Get-Content 'C:\Users\Nam\AppData\Local\Temp\javac_out.txt' -Raw)
    Remove-Item 'C:\Users\Nam\AppData\Local\Temp\javac_out.txt' -Force -ErrorAction SilentlyContinue
}
if (Test-Path 'C:\Users\Nam\AppData\Local\Temp\javac_err.txt') {
    $CompileOutput += (Get-Content 'C:\Users\Nam\AppData\Local\Temp\javac_err.txt' -Raw)
    Remove-Item 'C:\Users\Nam\AppData\Local\Temp\javac_err.txt' -Force -ErrorAction SilentlyContinue
}

if (Test-Path $SourcesFile) {
    Remove-Item $SourcesFile -Force -ErrorAction SilentlyContinue
}

if ($CompileProcess.ExitCode -ne 0) {
    Write-Log '[LỖI BIÊN DỊCH]:'
    Write-Log $CompileOutput
    Write-Log '================================================================================'
    Write-Log 'HỎNG'
    Write-Log '================================================================================'
    $OutputLines | Out-File -FilePath $LogFile -Encoding utf8
    exit 1
}

Write-Log ('Biên dịch thành công ' + $JavaFiles.Count + ' tệp Java!')

# 3. Tìm thư viện JUnit
$JunitJars = Get-ChildItem -Path (Join-Path $ProjectRoot 'lib') -Filter 'junit-platform-console-standalone-*.jar'
if ($null -eq $JunitJars -or $JunitJars.Count -eq 0) {
    Write-Log '[LỖI] Không tìm thấy thư viện junit-platform-console-standalone trong lib/!'
    Write-Log '================================================================================'
    Write-Log 'HỎNG'
    Write-Log '================================================================================'
    $OutputLines | Out-File -FilePath $LogFile -Encoding utf8
    exit 1
}
$JunitJarPath = Resolve-Path -Relative $JunitJars[0].FullName

# 4. Chạy kiểm thử JUnit 5
Write-Log ('[3/3] Thực thi kiểm thử JUnit 5 (Số lần lặp: ' + $Times + ')...')

$TongLanDat = 0
$CoLoi = $false

for ($i = 1; $i -le $Times; $i++) {
    Write-Log ''
    Write-Log '--------------------------------------------------------------------------------'
    Write-Log ('[LẦN CHẠY ' + $i + ' / ' + $Times + ']')
    Write-Log '--------------------------------------------------------------------------------'

    $TestOutFile = ('C:\Users\Nam\AppData\Local\Temp\junit_test_' + $i + '.txt')
    $TestProcess = Start-Process -FilePath 'java' -ArgumentList @('-jar', $JunitJarPath, 'execute', '--class-path', 'bin', '--scan-class-path', '--disable-banner', '--disable-ansi-colors') -NoNewWindow -PassThru -Wait -RedirectStandardOutput $TestOutFile -RedirectStandardError ($TestOutFile + '.err')

    $TestOutput = ''
    if (Test-Path $TestOutFile) {
        $TestOutput += (Get-Content $TestOutFile -Raw)
        Remove-Item $TestOutFile -Force -ErrorAction SilentlyContinue
    }
    if (Test-Path ($TestOutFile + '.err')) {
        $TestOutput += (Get-Content ($TestOutFile + '.err') -Raw)
        Remove-Item ($TestOutFile + '.err') -Force -ErrorAction SilentlyContinue
    }

    foreach ($line in ($TestOutput -split '\r?\n')) {
        if ($line.Trim() -ne '') {
            Write-Log $line
        }
    }

    if ($TestProcess.ExitCode -eq 0) {
        $TongLanDat++
        Write-Log ('=> Kết quả lần ' + $i + ': ĐẠT')
    } else {
        $CoLoi = $true
        Write-Log ('=> Kết quả lần ' + $i + ': HỎNG')
    }
}

# 5. Tổng kết
Write-Log ''
Write-Log '================================================================================'
if (-not $CoLoi) {
    Write-Log 'ĐẠT'
    Write-Log '================================================================================'
    Write-Log ('Chi tiết: ' + $TongLanDat + '/' + $Times + ' lần kiểm thử đều thành công.')
    Write-Log ('Kết quả kiểm thử đã được lưu vào: ' + $LogFile)
    $OutputLines | Out-File -FilePath $LogFile -Encoding utf8
    exit 0
} else {
    Write-Log 'HỎNG'
    Write-Log '================================================================================'
    Write-Log ('Chi tiết: ' + $TongLanDat + '/' + $Times + ' lần kiểm thử thành công, có lần kiểm thử bị lỗi.')
    Write-Log ('Kết quả kiểm thử đã được lưu vào: ' + $LogFile)
    $OutputLines | Out-File -FilePath $LogFile -Encoding utf8
    exit 1
}