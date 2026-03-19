# cleanup-test-data.ps1
# 用法示例:
#   .\cleanup-test-data.ps1 -BaseUrl 'http://localhost:8080' -Username 'admin' -Password 'admin123' -DryRun
param(
  [string]$BaseUrl = 'http://localhost:8080',
  [string]$Username = 'admin',
  [string]$Password = 'admin123',
  [switch]$DryRun
)

Write-Host "Connecting to $BaseUrl ..."

# 登录获取 token
try {
  $loginBody = @{ username = $Username; password = $Password } | ConvertTo-Json
  $loginResp = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post -ContentType 'application/json' -Body $loginBody -ErrorAction Stop
  $token = $loginResp.token ?? $loginResp.data?.token
  if (-not $token) { Write-Error "未获取到 token，登录返回：`n$($loginResp | ConvertTo-Json -Depth 5)"; exit 1 }
  Write-Host "Login success, token obtained."
} catch {
  Write-Error "登录失败: $_"
  exit 1
}

$headers = @{ Authorization = "Bearer $token"; 'Content-Type' = 'application/json' }

# 获取药品列表
try {
  $listResp = Invoke-RestMethod -Uri "$BaseUrl/drug/list" -Method Get -Headers $headers -ErrorAction Stop
} catch {
  Write-Error "获取药品列表失败: $_"
  exit 1
}

# 解析列表（兼容多种返回格式）
if ($listResp -is [System.Array]) {
  $drugs = $listResp
} elseif ($listResp.data -and ($listResp.data -is [System.Array])) {
  $drugs = $listResp.data
} elseif ($listResp.data -and $listResp.data.data -and ($listResp.data.data -is [System.Array])) {
  $drugs = $listResp.data.data
} else {
  $drugs = @()
}

# 匹配测试关键字
$pattern = 'test|mock|药A|DC001|BATCH1'
$candidates = $drugs | Where-Object { ($_.'drugName' -match $pattern) -or ($_.'drugCode' -match $pattern) -or ($_.'batchNo' -match $pattern) }

if (-not $candidates -or $candidates.Count -eq 0) {
  Write-Host "没有找到匹配的测试数据。"
  exit 0
}

Write-Host "找到 $($candidates.Count) 条可能的测试记录："
$candidates | Select-Object id, drugCode, drugName, batchNo | Format-Table -AutoSize

if ($DryRun) {
  Write-Host "Dry run mode, 未执行删除。"; exit 0
}

$confirm = Read-Host "确认要将以上记录标记为停售(status=discontinued)吗？输入 y 确认"
if ($confirm -ne 'y') { Write-Host "取消操作"; exit 0 }

# 执行更新（PUT /drug/update）
foreach ($c in $candidates) {
  $body = @{ id = $c.id; status = 'discontinued' } | ConvertTo-Json
  try {
    $resp = Invoke-RestMethod -Uri "$BaseUrl/drug/update" -Method Put -Headers $headers -ContentType 'application/json' -Body $body -ErrorAction Stop
    Write-Host "已处理 id=$($c.id) code=$($c.drugCode) name=$($c.drugName) -> OK"
  } catch {
    Write-Error "处理 id=$($c.id) 失败: $_"
  }
}

Write-Host "操作完成。请在系统中复核。"