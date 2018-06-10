

CREATE  DATABASE seckill;  
USE seckill;  
  
CREATE TABLE seckill(  
    seckill_id  BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',  
    `name`  VARCHAR(120)  NOT NULL COMMENT '商品名称',  
    number INT NOT NULL COMMENT '库存数量',  
    start_time TIMESTAMP NOT NULL COMMENT '秒杀开启时间',  
    end_time TIMESTAMP NOT NULL COMMENT '秒杀结束时间',  
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',  
    PRIMARY KEY (seckill_id),  
    KEY idx_start_time(start_time),  
    KEY idx_end_time(end_time),  
    KEY idx_create_time(create_time)  
  
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT '秒杀库存表'  
  
  
--初始化数据  
INSERT INTO seckill(NAME,number,start_time,end_time)  
VALUES  
('4000元秒杀iphoneX',300,'2018-6-10 00:00:00','2018-6-11 00:00:00'),  
('3000元秒杀坚果R1',200,'2018-6-10 00:00:00','2018-6-11 00:00:00'),  
('2000元秒杀小米笔记本',100,'2018-6-10 00:00:00','2018-6-11 00:00:00'),  
('1000元秒杀小米8',100,'2018-6-10 00:00:00','2018-6-11 00:00:00');  
  
--秒杀成功明细表  
--用户登录认证相关的信息  
CREATE TABLE success_kill(  
    seckill_id  BIGINT NOT NULL AUTO_INCREMENT COMMENT '秒杀商品id',  
    user_phone  BIGINT NOT NULL COMMENT '用户手机号',  
    state  TINYINT NOT NULL DEFAULT-1 COMMENT '状态标识，-1无效，0成功，1已付款',  
    create_time TIMESTAMP NOT NULL COMMENT '创建时间',  
    PRIMARY KEY(seckill_id,user_phone),  
    KEY idx_create_time(create_time)  
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT '秒杀成功明细表'  
  
SELECT * FROM seckill;  
SELECT * FROM success_kill;


------------------------------------------------------------------------------

DELIMITER $$  
  
CREATE PROCEDURE seckill.execute_seckill  
  (IN v_seckill_id BIGINT, IN v_phone BIGINT,  
   IN v_kill_time TIMESTAMP, OUT r_result INT)  
  BEGIN  
    DECLARE insert_count INT DEFAULT 0;  
    START TRANSACTION;  
    INSERT IGNORE INTO success_kill(seckill_id,user_phone,create_time,state)  
        VALUE(v_seckill_id,v_phone,v_kill_time,0);  
    SELECT ROW_COUNT() INTO insert_count;  
    IF(insert_count = 0) THEN  
       ROLLBACK;  
       SET r_result = -1;  
    ELSEIF(insert_count < 0) THEN  
       ROLLBACK;  
       SET r_result = -2;  
    ELSE  
       UPDATE seckill  
       SET number = number - 1  
       WHERE seckill_id = v_seckill_id  
         AND end_time > v_kill_time  
         AND start_time < v_kill_time  
         AND number > 0;  
       SELECT ROW_COUNT() INTO insert_count;  
       IF(insert_count = 0) THEN  
         ROLLBACK;  
         SET r_result = 0;  
       ELSEIF (insert_count < 0) THEN  
          ROLLBACK;  
          SET r_result = -2;  
        ELSE   
          COMMIT;  
          SET r_result = 1;  
        END IF;   
    END IF;  
   END;  
$$  
  
DELIMITER ;  
  
SET @r_result = -3;  
CALL execute_seckill(1000,13566668888,NOW(),@r_result);  
SELECT @r_result;  



