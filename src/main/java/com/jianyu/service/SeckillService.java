package com.jianyu.service;

import java.util.List;

import com.jianyu.dto.Exposer;
import com.jianyu.dto.SeckillExecution;
import com.jianyu.entity.Seckill;

public interface SeckillService {
	
	/**
	 * 获取秒杀商品列表（翻页查询）
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * 根据ID返回秒杀商品详情
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	
	/**
	 * 输出秒杀开启接口地址 
	 * @param seckillId
	 * @return
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * 执行秒杀动作，返回秒杀结果
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	SeckillExecution executeSeckill(long seckillId, long userPhone, String md5);
	
	/**
	 * 调用存储过程执行秒杀结果
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5);
	
}
