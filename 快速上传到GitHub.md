# 快速上传代码到GitHub

## 📋 当前状态

根据 `git status` 显示：
- ✅ 已有部分文件在暂存区（准备提交）
- 📝 新增的实体类和Mapper文件还未添加
- 🔧 `docker-compose.yml` 已修改但未暂存

## 🚀 快速上传步骤

### 方法一：使用命令行（推荐）

#### 步骤1：添加所有新文件

```bash
# 添加所有新文件（包括实体类、Mapper、配置文件等）
git add .

# 或者只添加特定文件
git add backend/huakang-mapper/src/main/java/com/huakang/mapper/
git add backend/huakang-mapper/src/main/java/com/huakang/mapper/entity/
git add "实体类和Mapper规划文档.md"
git add "Docker Compose配置指南.md"
git add docker-compose.yml
```

#### 步骤2：提交更改

```bash
git commit -m "feat: 添加实体类和Mapper接口

- 添加6个实体类：Admin, Staff, SystemConfig, Member, Product, PointsRecord
- 添加6个Mapper接口
- 添加MyBatis-Plus自动填充处理器
- 更新docker-compose.yml配置
- 添加Docker Compose配置指南
- 添加实体类和Mapper规划文档"
```

#### 步骤3：推送到GitHub

```bash
git push origin main
```

### 方法二：使用Git GUI工具

1. **GitHub Desktop**
   - 打开GitHub Desktop
   - 在左侧看到所有更改的文件
   - 填写提交信息
   - 点击"Commit to main"
   - 点击"Push origin"

2. **VS Code**
   - 打开源代码管理面板（Ctrl+Shift+G）
   - 点击"+"号暂存所有更改
   - 填写提交信息
   - 点击"✓"提交
   - 点击"..."菜单，选择"推送"

## 📝 提交信息规范

推荐使用以下格式：

```
feat: 添加实体类和Mapper接口

- 添加6个实体类：Admin, Staff, SystemConfig, Member, Product, PointsRecord
- 添加6个Mapper接口
- 添加MyBatis-Plus自动填充处理器
- 更新docker-compose.yml配置
- 添加Docker Compose配置指南
- 添加实体类和Mapper规划文档
```

**提交类型说明：**
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具相关

## ✅ 验证上传

上传完成后，访问GitHub仓库，确认：
- [ ] 所有实体类文件已上传
- [ ] 所有Mapper接口已上传
- [ ] 配置文件已更新
- [ ] 文档已上传

## 🔍 如果遇到问题

### 问题1：推送被拒绝

```bash
# 先拉取远程更改
git pull origin main

# 解决冲突后再次推送
git push origin main
```

### 问题2：文件太大

如果某些文件太大，可以：
1. 添加到 `.gitignore`
2. 使用Git LFS（大文件存储）

### 问题3：忘记添加某些文件

```bash
# 添加遗漏的文件
git add <文件路径>

# 修改上次提交（如果还没推送）
git commit --amend

# 或者创建新提交
git commit -m "fix: 添加遗漏的文件"
```

---

**按照以上步骤操作即可完成上传！** 🎉
