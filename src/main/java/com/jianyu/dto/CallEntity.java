package com.jianyu.dto;

import java.sql.Date;

public class CallEntity {
	long seckillId;
	long userPhone;
	Date killTime;
	int result;
	
	public long getSeckillId() {
		return seckillId;
	}
	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}
	public long getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}
	public Date getKillTime() {
		return killTime;
	}
	public void setKillTime(Date killTime) {
		this.killTime = killTime;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	
}
