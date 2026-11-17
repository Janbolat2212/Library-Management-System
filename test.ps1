$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = [Console]::OutputEncoding

& (Join-Path $PSScriptRoot "build.ps1")
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

$testClassesDir = Join-Path $PSScriptRoot "out\test-classes"
$allSources = @(
    Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot "src\main\java") -Filter "*.java" -Recurse
    Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot "src\test\java") -Filter "*.java" -Recurse
) |
    Select-Object -ExpandProperty FullName

New-Item -ItemType Directory -Path $testClassesDir -Force | Out-Null
& javac -encoding UTF-8 -d $testClassesDir $allSources
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

& java '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Dstderr.encoding=UTF-8' -ea -cp $testClassesDir kz.library.LibrarySystemTest
