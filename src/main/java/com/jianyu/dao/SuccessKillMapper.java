package com.jianyu.dao;

import org.apache.ibatis.annotations.Param;

import com.jianyu.entity.SuccessKill;

public interface SuccessKillMapper {
	/**
	 * 插入秒杀成功结果
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	int insertSuccessKill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

	/**
	 * 根据ID查询秒杀成功详情
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	SuccessKill queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
