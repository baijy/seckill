package com.jianyu.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jianyu.dto.CallEntity;
import com.jianyu.entity.Seckill;

public interface SeckillMapper {
	/**
	 * 减少商品库存
	 * 
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

	/**
	 * 根据ID查询商品
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);

	/**
	 * 查询所有商品（翻页查询）
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
	
	/**
	 * 使用存储过程秒杀商品
	 * @param paramMap
	 */
	public void seckillByProcedure(Map<String, Object> paramMap);
	
	/**
	 * 使用存储过程秒杀商品
	 * @param paramMap
	 */
	public void seckillByProcedureWith(CallEntity entity);
}
