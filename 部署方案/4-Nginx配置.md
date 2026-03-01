# Nginx 配置指南

## 目录
1. [Nginx 基础配置](#nginx-基础配置)
2. [反向代理配置](#反向代理配置)
3. [HTTPS 配置](#https-配置)
4. [性能优化](#性能优化)
5. [安全配置](#安全配置)
6. [常见问题](#常见问题)

---

## Nginx 基础配置

### 1. 配置文件结构

```
/etc/nginx/
├── nginx.conf              # 主配置文件
├── conf.d/                 # 站点配置目录
│   └── huakang.conf       # 项目配置文件
├── ssl/                    # SSL证书目录
│   ├── cert.pem
│   └── key.pem
└── logs/                   # 日志目录
    ├── access.log
    └── error.log
```

### 2. 创建项目配置文件

```bash
sudo vi /etc/nginx/conf.d/huakang.conf
```

---

## 反向代理配置

### 基础配置（HTTP）

```nginx
# 后端服务器配置
upstream huakang_backend {
    server 127.0.0.1:8080 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

# HTTP 服务器配置
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;
    
    # 访问日志
    access_log /var/log/nginx/huakang-access.log;
    error_log /var/log/nginx/huakang-error.log;
    
    # 客户端上传文件大小限制
    client_max_body_size 20M;
    
    # API 接口代理
    location /api/ {
        proxy_pass http://huakang_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # WebSocket 支持（如果需要）
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # 静态文件（前端）
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # 健康检查
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

### 测试配置

```bash
# 测试配置文件语法
sudo nginx -t

# 重载配置
sudo nginx -s reload

# 或重启 Nginx
sudo systemctl restart nginx
```

### 验证代理

```bash
# 测试 API 接口
curl http://your-domain.com/api/admin/health

# 预期返回
{"status":"UP"}
```

---

## HTTPS 配置

### 1. 获取 SSL 证书

#### 方式1：使用 Let's Encrypt（免费，推荐）

```bash
# 安装 certbot
sudo yum install -y certbot python3-certbot-nginx

# 或 Ubuntu
sudo apt install -y certbot python3-certbot-nginx

# 自动配置 HTTPS
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# 按提示输入邮箱和同意协议
```

#### 方式2：使用阿里云 SSL 证书

1. 登录阿里云控制台
2. 进入"SSL证书"服务
3. 申请免费证书（DV SSL，1年有效期）
4. 下载证书（选择 Nginx 格式）
5. 上传到服务器

```bash
# 创建证书目录
sudo mkdir -p /etc/nginx/ssl

# 上传证书文件
scp cert.pem root@123.456.789.100:/etc/nginx/ssl/
scp key.pem root@123.456.789.100:/etc/nginx/ssl/

# 设置权限
sudo chmod 600 /etc/nginx/ssl/key.pem
```

### 2. 配置 HTTPS

```bash
sudo vi /etc/nginx/conf.d/huakang.conf
```

**完整 HTTPS 配置：**

```nginx
# 后端服务器配置
upstream huakang_backend {
    server 127.0.0.1:8080 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;
    
    # 重定向到 HTTPS
    return 301 https://$server_name$request_uri;
}

# HTTPS 服务器配置
server {
    listen 443 ssl http2;
    server_name your-domain.com www.your-domain.com;
    
    # SSL 证书配置
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    
    # SSL 协议和加密套件
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers on;
    
    # SSL 会话缓存
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    
    # HSTS（强制 HTTPS）
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    
    # 访问日志
    access_log /var/log/nginx/huakang-access.log;
    error_log /var/log/nginx/huakang-error.log;
    
    # 客户端上传文件大小限制
    client_max_body_size 20M;
    
    # API 接口代理
    location /api/ {
        proxy_pass http://huakang_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # WebSocket 支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # 静态文件（前端）
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
        
        # 缓存配置
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
    
    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        root /usr/share/nginx/html;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
    
    # 健康检查
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

### 3. 测试 HTTPS

```bash
# 测试配置
sudo nginx -t

# 重载配置
sudo nginx -s reload

# 测试 HTTPS 访问
curl https://your-domain.com/api/admin/health
```

### 4. 自动续期证书（Let's Encrypt）

```bash
# 测试自动续期
sudo certbot renew --dry-run

# 添加定时任务
sudo crontab -e

# 添加以下行（每天凌晨2点检查并续期）
0 2 * * * /usr/bin/certbot renew --quiet && /usr/bin/nginx -s reload
```

---

## 性能优化

### 1. 启用 Gzip 压缩

```bash
sudo vi /etc/nginx/nginx.conf
```

**在 http 块中添加：**

```nginx
http {
    # Gzip 压缩配置
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript 
               application/json application/javascript application/xml+rss 
               application/rss+xml font/truetype font/opentype 
               application/vnd.ms-fontobject image/svg+xml;
    gzip_disable "msie6";
    
    # 其他配置...
}
```

### 2. 配置缓存

```nginx
# 在 server 块中添加
location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
    root /usr/share/nginx/html;
    expires 30d;
    add_header Cache-Control "public, immutable";
    access_log off;
}
```

### 3. 启用 HTTP/2

```nginx
# 在 HTTPS server 块中
listen 443 ssl http2;
```

### 4. 优化连接数

```bash
sudo vi /etc/nginx/nginx.conf
```

```nginx
events {
    worker_connections 2048;
    use epoll;
    multi_accept on;
}

http {
    keepalive_timeout 65;
    keepalive_requests 100;
    
    # 其他配置...
}
```

---

## 安全配置

### 1. 隐藏 Nginx 版本号

```nginx
http {
    server_tokens off;
    
    # 其他配置...
}
```

### 2. 防止点击劫持

```nginx
server {
    # 在 server 块中添加
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
}
```

### 3. 限制请求速率

```nginx
http {
    # 定义限流区域
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;
    
    server {
        location /api/ {
            # 应用限流
            limit_req zone=api_limit burst=20 nodelay;
            
            # 其他配置...
        }
    }
}
```

### 4. IP 黑名单

```nginx
# 创建黑名单文件
sudo vi /etc/nginx/conf.d/blacklist.conf
```

```nginx
# 禁止特定 IP 访问
deny 192.168.1.100;
deny 10.0.0.0/8;

# 允许其他所有 IP
allow all;
```

**在主配置中引入：**

```nginx
server {
    include /etc/nginx/conf.d/blacklist.conf;
    
    # 其他配置...
}
```

---

## 常见问题

### Q1: 502 Bad Gateway

**原因：** 后端服务未启动或无法连接

**解决方案：**

```bash
# 检查后端服务是否启动
sudo systemctl status huakang-admin

# 检查端口是否监听
sudo netstat -tlnp | grep 8080

# 查看 Nginx 错误日志
sudo tail -f /var/log/nginx/huakang-error.log
```

### Q2: 413 Request Entity Too Large

**原因：** 上传文件超过限制

**解决方案：**

```nginx
server {
    # 增加上传文件大小限制
    client_max_body_size 50M;
}
```

### Q3: 504 Gateway Timeout

**原因：** 后端处理超时

**解决方案：**

```nginx
location /api/ {
    # 增加超时时间
    proxy_connect_timeout 120s;
    proxy_send_timeout 120s;
    proxy_read_timeout 120s;
}
```

### Q4: HTTPS 证书错误

**解决方案：**

```bash
# 检查证书文件是否存在
ls -l /etc/nginx/ssl/

# 检查证书有效期
openssl x509 -in /etc/nginx/ssl/cert.pem -noout -dates

# 测试 SSL 配置
sudo nginx -t
```

### Q5: 静态文件 404

**解决方案：**

```bash
# 检查文件路径是否正确
ls -l /usr/share/nginx/html/

# 检查文件权限
sudo chmod -R 755 /usr/share/nginx/html/
```

---

## 日志分析

### 1. 查看访问日志

```bash
# 实时查看访问日志
sudo tail -f /var/log/nginx/huakang-access.log

# 统计访问最多的 IP
sudo awk '{print $1}' /var/log/nginx/huakang-access.log | sort | uniq -c | sort -rn | head -10

# 统计访问最多的 URL
sudo awk '{print $7}' /var/log/nginx/huakang-access.log | sort | uniq -c | sort -rn | head -10

# 统计状态码分布
sudo awk '{print $9}' /var/log/nginx/huakang-access.log | sort | uniq -c | sort -rn
```

### 2. 查看错误日志

```bash
# 实时查看错误日志
sudo tail -f /var/log/nginx/huakang-error.log

# 统计错误类型
sudo grep -oP '\[error\] \d+#\d+: \*\d+ \K[^,]+' /var/log/nginx/huakang-error.log | sort | uniq -c | sort -rn
```

---

## 完整配置示例

**生产环境推荐配置：**

```nginx
# /etc/nginx/conf.d/huakang.conf

# 后端服务器配置
upstream huakang_backend {
    server 127.0.0.1:8080 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;
    return 301 https://$server_name$request_uri;
}

# HTTPS 服务器配置
server {
    listen 443 ssl http2;
    server_name your-domain.com www.your-domain.com;
    
    # SSL 证书
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256';
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    
    # 安全头
    add_header Strict-Transport-Security "max-age=31536000" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    
    # 日志
    access_log /var/log/nginx/huakang-access.log;
    error_log /var/log/nginx/huakang-error.log;
    
    # 上传限制
    client_max_body_size 20M;
    
    # API 代理
    location /api/ {
        proxy_pass http://huakang_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # 静态文件
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
    
    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2)$ {
        root /usr/share/nginx/html;
        expires 30d;
        add_header Cache-Control "public, immutable";
        access_log off;
    }
}
```

---

**文档版本**：v1.0  
**更新日期**：2026-02-15  
**适用项目**：华康电器积分系统
