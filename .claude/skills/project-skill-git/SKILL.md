---
name: project-skill-git
description: 当用户要提交代码、push 到 GitHub/GitLab、或说"提交一下"/"git commit"/"推送代码"时使用。分析本轮变更内容，自动跳过敏感文件进行暂存，生成规范的中文 Conventional Commit 消息（80字以内），并在用户明确要求时推送到远端。任何涉及 git 提交的操作都应触发此技能。
---

# 代码提交规范

## 核心流程

**第一步：分析变更**

运行以下命令了解本轮改动范围：

```bash
git status
git diff --stat HEAD
```

若有必要（文件较多或变更复杂），再看 `git diff HEAD` 了解具体内容。

**第二步：智能暂存**

先检查哪些文件需要跳过，再执行暂存：

跳过以下类型（不加入 staging）：
- `.env`、`*.env`、`.env.*`、`.env.local`
- `*.log`、`logs/`
- `.DS_Store`、`Thumbs.db`
- `*.class`、`target/`（Maven 构建产物）
- `node_modules/`、`dist/`（前端构建产物）

若发现敏感文件存在但未被 `.gitignore` 覆盖，**提示用户**后再继续，不要静默跳过。

执行暂存：

```bash
git add -A
# 若有敏感文件未被 .gitignore 覆盖，单独排除：
# git reset HEAD <sensitive-file>
```

**第三步：生成 commit message**

格式：
```
type(scope): 简洁中文描述
```

- **type** 对照表：

  | type | 用途 |
  |------|------|
  | `feat` | 新功能、新接口、新页面 |
  | `fix` | Bug 修复 |
  | `refactor` | 重构（不改变对外行为） |
  | `test` | 补充或修改测试 |
  | `docs` | 文档、注释 |
  | `chore` | 构建配置、依赖、脚手架 |
  | `style` | 前端样式（CSS/UI 调整） |
  | `perf` | 性能优化 |

- **scope** 写模块名（可选）：如 `账单`、`支付方式`、`统计`、`分类`、`用户`

- **描述**：
  - 用中文，动宾结构开头，如"新增…"、"修复…"、"重构…"
  - **不超过 80 字**（含 type 和 scope 前缀）
  - 不写"本次"、"这次"等废话词
  - 多个独立变更，取主要变更为主题，其余可加到正文

- 若变更跨越多个模块，scope 可省略，直接写综合描述。

**示例：**
```
feat(支付方式): 新增支付方式分页查询接口及前端列表页
fix(账单): 修复账单列表金额格式化为负数的显示错误
refactor(支付方式): 重构 PaymentMethodServiceImpl 分页查询逻辑
test(支付方式): 补充 PaymentMethodServiceImpl 单元测试覆盖边界值
chore: 更新 .gitignore 忽略 target/ 和日志文件
```

**第四步：提交**

```bash
git commit -m "$(cat <<'EOF'
type(scope): 描述内容
EOF
)"
```

提交后展示给用户确认，输出：
```
✅ 已提交：type(scope): 描述内容
```

**第五步：推送（仅用户明确要求时执行）**

用户说了"推送"、"push"、"提交到 GitHub/GitLab"、"推上去"等才执行：

```bash
git push
```

若当前分支无上游：
```bash
git push -u origin <current-branch>
```

**不要**在用户只说"提交"时自动 push。

---

## 用户意图对照

| 用户说 | 执行动作 |
|--------|---------|
| "帮我提交" / "commit 一下" | 仅 commit，不 push |
| "提交并推送" / "push 上去" | commit + push |
| "只 push" | 直接 push（跳过暂存和 commit）|
| "提交 xxx 文件" | 只暂存指定文件后 commit |

---

## 注意事项

- 若当前没有任何变更（`git status` 显示 clean），告知用户无需提交
- 若存在合并冲突，停止并提示用户先解决冲突
- 不使用 `--no-verify` 跳过 git hooks，hooks 失败时报告原因
- 不强制推送（`--force`）到 main/master，如用户执意如此先二次确认
