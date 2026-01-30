#!/bin/bash

# 华康电器连锁积分小程序 - 数据库备份脚本

set -e

# 配置
BACKUP_DIR="/opt/huakang/backups/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="hk_electronics"
DB_USER="${DB_USERNAME:-hk_prod}"
DB_PASS="${DB_PASSWORD}"
RETENTION_DAYS=30

# 创建备份目录
mkdir -p $BACKUP_DIR

echo "=========================================="
echo "开始备份数据库: $DB_NAME"
echo "时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="

# 备份数据库
mysqldump -u$DB_USER -p$DB_PASS \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    $DB_NAME | gzip > $BACKUP_DIR/${DB_NAME}_${DATE}.sql.gz

# 检查备份是否成功
if [ $? -eq 0 ]; then
    echo "✓ 备份成功: ${DB_NAME}_${DATE}.sql.gz"
    
    # 获取备份文件大小
    SIZE=$(du -h $BACKUP_DIR/${DB_NAME}_${DATE}.sql.gz | cut -f1)
    echo "✓ 备份文件大小: $SIZE"
else
    echo "✗ 备份失败"
    exit 1
fi

# 删除旧备份
echo "清理 ${RETENTION_DAYS} 天前的备份..."
find $BACKUP_DIR -name "${DB_NAME}_*.sql.gz" -mtime +$RETENTION_DAYS -delete

# 显示当前备份列表
echo "=========================================="
echo "当前备份列表:"
ls -lh $BACKUP_DIR/${DB_NAME}_*.sql.gz | tail -5
echo "=========================================="

echo "备份完成！"
