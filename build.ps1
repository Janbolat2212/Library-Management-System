$ErrorActionPreference = "Stop"

$projectRoot = $PSScriptRoot
$classesDir = Join-Path $projectRoot "out\classes"
$sources = Get-ChildItem -LiteralPath (Join-Path $projectRoot "src\main\java") -Filter "*.java" -Recurse |
    Select-Object -ExpandProperty FullName

if (-not $sources) {
    throw "Java source files were not found."
}

New-Item -ItemType Directory -Path $classesDir -Force | Out-Null
& javac -encoding UTF-8 -d $classesDir $sources
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host "Build completed: $classesDir"
