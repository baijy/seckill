package com.jianyu.exception;

/**
 * 秒杀关闭异常
 * @author BaiJianyu
 *
 */
public class SeckillCloseException extends RuntimeException {
	public SeckillCloseException(String message) {
		super(message);
	}

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}
}
