package com.jianyu.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.jianyu.cache.RedisDao;
import com.jianyu.dao.SeckillMapper;
import com.jianyu.dao.SuccessKillMapper;
import com.jianyu.dto.Exposer;
import com.jianyu.dto.SeckillExecution;
import com.jianyu.entity.Seckill;
import com.jianyu.entity.SuccessKill;
import com.jianyu.enums.SeckillStateEnum;
import com.jianyu.exception.RepeatKillException;
import com.jianyu.exception.SeckillCloseException;
import com.jianyu.exception.SeckillException;
import com.jianyu.service.SeckillService;

/**
 * 实现类
 * 
 * @author BaiJianyu
 *
 */
@Service
public class SeckillServiceImpl implements SeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillMapper seckillMapper;

	@Autowired
	private SuccessKillMapper successKillMapper;

	@Autowired
	private RedisDao redisDao;

	private final String salt = "kXJT7k8iRBy2Nuv%$m8613XhveZOg$@WwQ";

	/**
	 * 获取秒杀商品列表（翻页查询）
	 * 
	 * @return
	 */
	@Override
	public List<Seckill> getSeckillList() {
		return seckillMapper.queryAll(0, 4);
	}

	/**
	 * 根据ID查询商品
	 * 
	 * @param seckillId
	 * @return
	 */
	@Override
	public Seckill getById(long seckillId) {
		return seckillMapper.queryById(seckillId);
	}

	/**
	 * 输出秒杀开启接口地址
	 * 
	 * @param seckillId
	 * @return
	 */
	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (null == seckill) {
			// 访问数据库
			seckill = seckillMapper.queryById(seckillId);
			if (null == seckill) {
				// 数据库查不到，不开启秒杀
				return new Exposer(false, seckillId);
			} else {
				redisDao.putSeckill(seckill);
			}
		}

		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		// 当前系统时间
		Date nowTime = new Date();
		// 未开始或者已经结束，则返回不开启秒杀
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}

		// 转换特定字符串的过程，不可逆
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	/**
	 * 执行秒杀动作，返回秒杀结果
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	@Override
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) {
		if (null == md5 || (!md5.equals(getMD5(seckillId)))) {
			throw new SeckillException("Seckill data rewrite");
		}

		Date nowTime = new Date();
		try {
			int insertCount = successKillMapper.insertSuccessKill(seckillId, userPhone);
			if (insertCount <= 0) {
				throw new RepeatKillException("Seckill repeated");
			} else {
				// 减库存
				int updateCount = seckillMapper.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					// 没有更新到记录，秒杀结束
					throw new SeckillCloseException("Seckill is closed");
				} else {
					// 秒杀成功
					SuccessKill successKilled = successKillMapper.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage());
			// 所有编译期异常转换为运行时异常
			throw new SeckillException("Seckill inner error" + e.getMessage());
		}
	}

	/**
	 * 调用存储过程执行秒杀结果
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	@Override
	public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
		if (md5 == null || !(md5.equals(getMD5(seckillId)))) {
			throw new SeckillException("Seckill data rewrite");
		}

		Date killTime = new Date();
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		
		// 执行存储过程，赋值result
		try {
			// 秒杀商品，获得结果
			// 只能用map调用，这样才可以接收返回值
			seckillMapper.seckillByProcedure(map);

			int result = MapUtils.getInteger(map, "result", -2);
			// 返回结果
			if (1 == result) {
				SuccessKill successKill = successKillMapper.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKill);
			} else {
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}

	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
}
