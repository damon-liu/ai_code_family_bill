---
name: backend-api-tdd-loop
description: 当 [auto-test] hook 反馈后端接口测试失败时，按本流程持续修复直至全部通过再向用户汇报。也适用于用户要求"新增/重构后端接口"时主动触发该闭环。
---

# 后端接口自动测试-修复闭环

## 触发时机

1. **被动**：`.claude/hooks/run-related-tests.sh` 因 `Write/Edit` 后端 Java 文件后跑测试失败，stderr 中出现 `❌ [auto-test]` 标记
2. **主动**：用户要求新增、修改、重构 controller / service / mapper / dto 时，主动按此流程开发

## 流程（严格执行，不得跳步）

### 步骤 1 — 接口设计落地
- DTO / Service 接口 / Service 实现 / Controller 一次性写完，遵循 `backend-skill-code` 全部约束
- Controller 仅返回 `Result<T>`，业务逻辑不下沉到 Controller

### 步骤 2 — 同步补齐 JUnit5 单元测试
- 每个新增/修改的 Service 方法必须有 `*ServiceImplTest` 覆盖
- 每个新增的 Controller 端点必须有 `*ControllerTest` 覆盖（用 `MockMvcBuilders.standaloneSetup`，避免加载 Spring 容器）
- 覆盖三类场景：**正常**、**边界**、**异常**

### 步骤 3 — 等待 hook 反馈
- 文件保存后 hook 会自动运行 `mvn -f backend/pom.xml -Dtest=<相关类> test`
- hook 输出可能形态：
  - `✅ [auto-test] ... 通过` → 步骤 4
  - `❌ [auto-test] ... 测试失败（第 N/3 次）` → 步骤 5（修复闭环）
  - `🛑 [auto-test] ... 已连续失败 3 次` → 步骤 6（**立即停止循环并汇报**）
  - `⚠️ ... 未找到对应测试类` → 立即返回步骤 2 补测试

### 步骤 4 — 通过后汇报
- 列出新增/修改的文件清单
- 列出测试用例数量与覆盖场景
- 给出接口签名（URL + 入参 + 出参）

### 步骤 5 — 失败修复闭环（核心）
hook 失败反馈中已附测试输出摘要。按以下顺序判断与修复，**禁止靠改测试期望让其通过**：

1. **断言失败**（`expected: X but was: Y`）
   - 优先怀疑生产代码逻辑（Service 计算错、SQL 条件错、字段映射错）
   - 仅在测试期望明显有误（如复制粘贴遗留的旧值）时才允许调整测试

2. **抛出异常**（`NullPointerException`、`IllegalArgumentException`...）
   - 通常是空指针未防御或参数转换问题，回到生产代码加空判断或类型转换

3. **Spring 上下文加载失败**（`Failed to load ApplicationContext`、`sqlSessionFactory required`）
   - 控制器测试不要用 `@WebMvcTest`，改为 `MockitoExtension + MockMvcBuilders.standaloneSetup`

4. **编译错误**
   - 检查 import、方法签名、泛型

修改完成后保存文件，hook 会自动重跑。**只要 stderr 仍出现 `❌ [auto-test]`，就回到本步骤继续修复，不得向用户汇报"完成"**。

### 步骤 6 — 达到循环上限后的强制汇报（看到 🛑 立即执行）

当 hook 输出 `🛑 [auto-test] ... 已连续失败 3 次` 时：

1. **立刻停止修改任何代码**，不要再 Edit / Write 任何文件试图"再试一次"
2. 按以下结构向用户汇报：
   - 失败的测试类与失败测试方法
   - 近 3 次失败的核心错误信息（断言不符 / 抛异常 / 编译错）
   - 你对根因的最佳猜测，分类列出：
     - 测试期望是否本身有误
     - 接口设计是否需要调整
     - 是否存在配置 / 依赖 / Spring 上下文加载等环境问题
   - 提供 2~3 个可选方案让用户决策（例：① 调整测试期望 ② 重新设计接口 ③ 把这一项作为已知遗留交给后续）
3. 等用户决策后再恢复修改

> 触发 🛑 的计数会被自动清零；下次保存若再次失败，重新从第 1/3 计起。

## 规约（硬约束）

- 测试通过的判定来源：hook 反馈，不是模型的主观判断
- 不允许通过删除 / 跳过 / 注释测试来让 hook "通过"
- 不允许把单测期望改成符合错误实现的值（除非用户明确同意）
- **不允许在收到 🛑 后继续修改代码绕过上限**
- 单类测试通过后建议跑一次 `mvn -f backend/pom.xml test` 全量回归（可选，由用户决定）
