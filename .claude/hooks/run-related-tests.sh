#!/usr/bin/env bash
# .claude/hooks/run-related-tests.sh
# 用途：Write/Edit 修改 backend Java 文件后自动定位并运行对应的 JUnit5 测试类
# 入参：Claude Code hook 通过 stdin 注入 JSON
# 出参：
#   - 测试通过 → exit 0（仅记录日志）
#   - 测试失败 → exit 2 + stderr 详情，自动反馈给 Claude 触发修复
#   - 未找到测试类 → exit 0 + 警告（提醒补写测试）

set -u

INPUT=$(cat)

# 用 Node 解析 JSON，提取 tool_input.file_path
FILE_PATH=$(printf '%s' "$INPUT" | node -e "
let raw='';
process.stdin.on('data', c => raw += c);
process.stdin.on('end', () => {
  try {
    const o = JSON.parse(raw);
    const fp = (o.tool_input && o.tool_input.file_path) || '';
    process.stdout.write(fp);
  } catch (e) {
    process.stdout.write('');
  }
});
" 2>/dev/null)

# 规范化路径分隔符
FILE_PATH=$(printf '%s' "$FILE_PATH" | tr '\\' '/')

# 非 backend Java 文件直接放行
if [[ ! "$FILE_PATH" =~ backend/src/.*\.java$ ]]; then
  exit 0
fi

CLASS_NAME=$(basename "$FILE_PATH" .java)

# 推算测试类：改测试文件就跑自己；改生产代码则找同名 + "Test"
if [[ "$FILE_PATH" =~ backend/src/test/ ]]; then
  TEST_CLASS="$CLASS_NAME"
else
  TEST_CLASS="${CLASS_NAME}Test"
fi

# 定位项目根（hook 工作目录就是项目根，但保险起见用相对路径校验）
if [[ ! -f "backend/pom.xml" ]]; then
  echo "⚠️  [auto-test] 未在项目根目录运行，跳过 (cwd=$(pwd))" >&2
  exit 0
fi

# 查找测试类文件
TEST_FILE=$(find backend/src/test -name "${TEST_CLASS}.java" -type f 2>/dev/null | head -1)

if [[ -z "$TEST_FILE" ]]; then
  cat >&2 <<EOF
⚠️  [auto-test] 已修改 ${CLASS_NAME}，但未找到对应测试类 ${TEST_CLASS}.java
   按 backend-skill-code 规范，新增/重构业务必须配套单元测试，请补写后再继续。
EOF
  # 仅警告不阻塞 —— 因为修改也可能是单纯的 entity / DTO / 工具类
  exit 0
fi

echo "[auto-test] 检测到 ${CLASS_NAME} 变更，运行 ${TEST_CLASS}..." >&2

# 跑测试（只跑这一个类，秒级反馈）
RESULT=$(mvn -f backend/pom.xml -Dtest="${TEST_CLASS}" -DfailIfNoTests=false test 2>&1)
EXIT_CODE=$?

# 摘出关键摘要行
SUMMARY=$(printf '%s' "$RESULT" | grep -E "Tests run:|<<< FAILURE|<<< ERROR|expected:|but was:|AssertionError|FAILED|BUILD " | head -40)

# -----------------------------------------------------------------------
# 循环次数上限：同一测试类连续失败 ≥ MAX_RETRIES 次则停止自动修复
# -----------------------------------------------------------------------
STATE_DIR=".claude/hooks/.state"
mkdir -p "$STATE_DIR"
COUNTER_FILE="$STATE_DIR/${TEST_CLASS}.count"
MAX_RETRIES=3

if [[ $EXIT_CODE -eq 0 ]]; then
  # 测试通过 → 清零计数
  rm -f "$COUNTER_FILE"
  echo "✅ [auto-test] ${TEST_CLASS} 通过" >&2
  printf '%s\n' "$SUMMARY" | grep -E "Tests run:" | head -1 >&2 || true
  exit 0
fi

# 测试失败 → 累加计数
CURRENT_COUNT=0
if [[ -f "$COUNTER_FILE" ]]; then
  CURRENT_COUNT=$(cat "$COUNTER_FILE" 2>/dev/null || echo 0)
fi
NEW_COUNT=$((CURRENT_COUNT + 1))

if [[ $NEW_COUNT -ge $MAX_RETRIES ]]; then
  # 达到上限 → 停止自动修复，要求 Claude 改为向用户汇报
  rm -f "$COUNTER_FILE"
  cat >&2 <<EOF
🛑 [auto-test] ${TEST_CLASS} 已连续失败 ${NEW_COUNT} 次（达到上限 ${MAX_RETRIES} 次），自动修复循环已暂停。

**必须停止继续修改代码**，改为按以下结构向用户汇报，由用户人工决策：
1. 列出本测试类近 ${MAX_RETRIES} 次失败的关键摘要
2. 给出你对失败根因的最佳猜测（测试期望有误？接口设计问题？依赖/配置缺失？）
3. 提供 2~3 个可选的后续方案让用户选择

最后一次测试输出摘要：
${SUMMARY}
EOF
  exit 2
fi

# 未达上限 → 正常反馈失败摘要，让 Claude 继续修
echo "$NEW_COUNT" > "$COUNTER_FILE"
cat >&2 <<EOF
❌ [auto-test] ${TEST_CLASS} 测试失败（第 ${NEW_COUNT}/${MAX_RETRIES} 次） —— 必须修复后才能视为完成。

测试输出摘要：
$SUMMARY

请按以下顺序处理：
1. 阅读上方失败信息，定位失败的断言或异常
2. 优先怀疑生产代码（Controller / Service / Mapper），其次再考虑测试本身有错
3. 修改实现，保存后会自动重跑测试
4. 当本提示不再出现时，再向用户汇报结果
EOF

exit 2
