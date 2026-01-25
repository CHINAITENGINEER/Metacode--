-- 积分对账存储过程
-- 检查会员积分总额与积分记录的一致性

DELIMITER $$

DROP PROCEDURE IF EXISTS `check_points_consistency`$$

CREATE PROCEDURE `check_points_consistency`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_member_id BIGINT;
    DECLARE v_calculated_points INT;
    DECLARE v_actual_points INT;
    DECLARE v_inconsistency_count INT DEFAULT 0;
    
    -- 游标声明
    DECLARE cur_members CURSOR FOR 
        SELECT id FROM members WHERE is_deleted = 0;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 创建临时表存储对账结果
    DROP TEMPORARY TABLE IF EXISTS reconciliation_results;
    CREATE TEMPORARY TABLE reconciliation_results (
        member_id BIGINT,
        calculated_points INT,
        actual_points INT,
        difference INT,
        status VARCHAR(20),
        checked_at DATETIME
    );
    
    -- 打开游标
    OPEN cur_members;
    
    read_loop: LOOP
        FETCH cur_members INTO v_member_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 计算积分记录中的积分总额（发放 - 消耗）
        SELECT 
            COALESCE(SUM(CASE 
                WHEN change_type IN ('add', 'consume_gift') THEN points
                WHEN change_type IN ('subtract', 'exchange') THEN -points
                ELSE 0
            END), 0) INTO v_calculated_points
        FROM points_records
        WHERE member_id = v_member_id AND is_deleted = 0;
        
        -- 获取会员表中的实际积分
        SELECT COALESCE(total_points, 0) INTO v_actual_points
        FROM members
        WHERE id = v_member_id;
        
        -- 如果计算值与实际值不一致，记录到临时表
        IF v_calculated_points != v_actual_points THEN
            INSERT INTO reconciliation_results 
            (member_id, calculated_points, actual_points, difference, status, checked_at)
            VALUES 
            (v_member_id, v_calculated_points, v_actual_points, 
             v_calculated_points - v_actual_points, 'INCONSISTENT', NOW());
            
            SET v_inconsistency_count = v_inconsistency_count + 1;
        END IF;
        
    END LOOP;
    
    CLOSE cur_members;
    
    -- 返回对账结果
    SELECT 
        member_id,
        calculated_points,
        actual_points,
        difference,
        status,
        checked_at
    FROM reconciliation_results
    ORDER BY ABS(difference) DESC;
    
    -- 返回统计信息
    SELECT 
        COUNT(*) as total_members_checked,
        v_inconsistency_count as inconsistent_count,
        NOW() as checked_at;
    
    -- 清理临时表
    DROP TEMPORARY TABLE IF EXISTS reconciliation_results;
    
END$$

DELIMITER ;
