param (
    [Parameter(Mandatory=$true)]
    [string]$InputFile,

    [Parameter(Mandatory=$false)]
    [string]$OutputFile,

    [Parameter(Mandatory=$false)]
    [int]$Scale = 2,

    [Parameter(Mandatory=$false)]
    [switch]$Transparent
)

# Resolve input file
if (-not (Test-Path $InputFile)) {
    Write-Error ("Tep nguon khong ton tai: " + $InputFile)
    exit 1
}
$resolvedIn = (Resolve-Path $InputFile).Path

# Resolve output file (.drawio.png)
if (-not $OutputFile) {
    if ($resolvedIn.EndsWith(".drawio")) {
        $OutputFile = [System.IO.Path]::ChangeExtension($resolvedIn, ".drawio.png")
    } else {
        $OutputFile = "$resolvedIn.drawio.png"
    }
} else {
    $outDir = [System.IO.Path]::GetDirectoryName($OutputFile)
    if ($outDir -and -not (Test-Path $outDir)) {
        New-Item -ItemType Directory -Path $outDir -Force | Out-Null
    }
    $OutputFile = [System.IO.Path]::GetFullPath($OutputFile)
}

# Locate draw.io executable
$drawioPaths = @(
    "C:\Program Files\draw.io\draw.io.exe",
    "C:\Program Files (x86)\draw.io\draw.io.exe",
    "$env:LOCALAPPDATA\Programs\draw.io\draw.io.exe"
)

$drawioExe = $null
foreach ($path in $drawioPaths) {
    if (Test-Path $path) {
        $drawioExe = $path
        break
    }
}

if (-not $drawioExe) {
    $cmd = Get-Command draw.io -ErrorAction SilentlyContinue
    if ($cmd) {
        $drawioExe = $cmd.Source
    }
}

if (-not $drawioExe) {
    Write-Error "Khong tim thay draw.io Desktop tren may! Vui long cai dat tu https://github.com/jgraph/drawio-desktop/releases"
    exit 1
}

Write-Host "Su dung Draw.io CLI: $drawioExe"
Write-Host "Dang xuat: $resolvedIn -> $OutputFile"

$argList = @("--no-sandbox", "--disable-gpu", "-x", "-f", "png", "-s", [string]$Scale, "-o", "`"$OutputFile`"", "`"$resolvedIn`"")
if ($Transparent) {
    $argList += "-t"
}

$proc = Start-Process -FilePath $drawioExe -ArgumentList $argList -Wait -PassThru -NoNewWindow

if (Test-Path $OutputFile) {
    Write-Host "Xuat so do thanh cong: $OutputFile" -ForegroundColor Green
    exit 0
} else {
    $code = $proc.ExitCode
    Write-Error "Loi khi xuat so do. Exit code: $code"
    exit 1
}
