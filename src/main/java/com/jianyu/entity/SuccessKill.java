package com.jianyu.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀成功实体类
 * @author BaiJianyu
 *
 */
public class SuccessKill implements Serializable {
	private static final long serialVersionUID = 7746707843134694339L;
	
	private long userPhone;  
    private short state;  
    private Date createTime;  
    private Seckill seckill;
    
	public long getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}
	public short getState() {
		return state;
	}
	public void setState(short state) {
		this.state = state;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Seckill getSeckill() {
		return seckill;
	}
	public void setSeckill(Seckill seckill) {
		this.seckill = seckill;
	}  
    
}
