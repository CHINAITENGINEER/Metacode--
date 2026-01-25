# GitHub推送问题解决

## ❌ 当前问题

推送时遇到错误：
```
git@github.com: Permission denied (publickey).
fatal: Could not read from remote repository.
```

**原因：** 使用SSH方式推送，但没有配置SSH密钥。

## ✅ 解决方案

### 方案一：使用HTTPS方式（推荐，最简单）

#### 步骤1：检查远程仓库地址

```bash
git remote -v
```

#### 步骤2：如果显示SSH地址，改为HTTPS

```bash
# 查看当前远程地址
git remote get-url origin

# 如果显示 git@github.com:用户名/仓库名.git
# 改为HTTPS地址
git remote set-url origin https://github.com/用户名/仓库名.git
```

#### 步骤3：重新推送

```bash
git push origin main
```

推送时会提示输入GitHub用户名和密码（或Personal Access Token）。

---

### 方案二：配置SSH密钥（适合长期使用）

#### 步骤1：生成SSH密钥

```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
```

按Enter使用默认路径，设置密码（可选）。

#### 步骤2：复制公钥

**Windows:**
```bash
type %USERPROFILE%\.ssh\id_ed25519.pub
```

**或者手动打开文件：**
`C:\Users\你的用户名\.ssh\id_ed25519.pub`

#### 步骤3：添加到GitHub

1. 登录GitHub
2. 点击右上角头像 → Settings
3. 左侧菜单选择 SSH and GPG keys
4. 点击 New SSH key
5. Title填写：`Windows PC`（任意名称）
6. Key粘贴刚才复制的公钥内容
7. 点击 Add SSH key

#### 步骤4：测试连接

```bash
ssh -T git@github.com
```

如果显示 `Hi 用户名! You've successfully authenticated...` 说明成功。

#### 步骤5：推送

```bash
git push origin main
```

---

### 方案三：使用Personal Access Token（HTTPS方式）

如果使用HTTPS方式，GitHub现在要求使用Personal Access Token而不是密码。

#### 步骤1：生成Token

1. 登录GitHub
2. 点击右上角头像 → Settings
3. 左侧菜单选择 Developer settings
4. 选择 Personal access tokens → Tokens (classic)
5. 点击 Generate new token (classic)
6. Note填写：`本地开发`
7. 勾选权限：`repo`（完整仓库权限）
8. 点击 Generate token
9. **复制Token**（只显示一次，请保存）

#### 步骤2：推送时使用Token

```bash
git push origin main
```

提示输入密码时：
- **用户名**：你的GitHub用户名
- **密码**：粘贴刚才生成的Personal Access Token

---

## 🔍 检查当前配置

运行以下命令检查：

```bash
# 查看远程仓库地址
git remote -v

# 查看当前分支
git branch

# 查看提交历史
git log --oneline -5
```

---

## 📝 推荐配置

**对于Windows用户，推荐使用HTTPS方式：**

1. 确保远程地址是HTTPS：
   ```bash
   git remote set-url origin https://github.com/用户名/仓库名.git
   ```

2. 使用Personal Access Token作为密码

3. 或者使用Git Credential Manager（Windows自带）：
   ```bash
   git config --global credential.helper manager-core
   ```

---

## ✅ 验证推送成功

推送成功后，访问GitHub仓库，确认：
- [ ] 所有实体类文件已上传
- [ ] 所有Mapper接口已上传
- [ ] 配置文件已更新
- [ ] 文档已上传

---

**按照以上方案操作即可完成推送！** 🎉
