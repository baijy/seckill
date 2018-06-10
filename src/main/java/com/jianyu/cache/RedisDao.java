package com.jianyu.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.jianyu.entity.Seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 缓存操作Dao
 * @author BaiJianyu
 *
 */
@Component
public class RedisDao {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private JedisPool jedisPool;
	private int port = 6379;
	private String ip= "127.0.0.1";

/*	public RedisDao() {
		logger.info("---- ip:{},port:{}", ip, port);
		this.port = port;
		this.ip = ip;
	}*/

	// Serialize function
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	
	/**
	 * 从缓存中取出
	 * @param seckillId
	 * @return
	 */
	public Seckill getSeckill(long seckillId) {
		jedisPool = new JedisPool(ip, port);
		// redis operate
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				// 由于redis内部没有实现序列化方法,而且jdk自带的implements Serializable比较慢,会影响并发,因此需要使用第三方序列化方法.
				byte[] bytes = jedis.get(key.getBytes());
				if (null != bytes) {
					Seckill seckill = schema.newMessage();
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					// reSerialize
					return seckill;
				}
			} finally {
				jedisPool.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}
	
	/**
	 * 写入缓存
	 * @param seckill
	 * @return
	 */
	public String putSeckill(Seckill seckill) {
		jedisPool = new JedisPool(ip, port);
		// set Object(seckill) ->Serialize -> byte[]
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				// time out cache
				int timeout = 60 * 60;
				String result = jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			} finally {
				jedisPool.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
}
