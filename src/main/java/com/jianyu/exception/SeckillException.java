package com.jianyu.exception;

/**
 * 通用秒杀异常
 * @author BaiJianyu
 *
 */
public class SeckillException extends RuntimeException {
	public SeckillException(String message) {
		super(message);
	}

	public SeckillException(String message, Throwable cause) {
		super(message, cause);
	}
}
