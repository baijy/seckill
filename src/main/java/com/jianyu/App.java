package com.jianyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 参考文章：https://blog.csdn.net/sdksdk0/article/details/52997034 
 * 参考工程：https://github.com/sdksdk0/SecKill 
 * @author BaiJianyu
 *
 */
@SpringBootApplication
@MapperScan("com.jianyu.dao")
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
