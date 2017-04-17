package com.taotao.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestJedisSpring {
	@Test
	public void testJedisClientPoll() throws Exception{
		//初始化spring容器
		ApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//从容器中获得jedisClient对象
		JedisClient jedisClient= applicationContext.getBean(JedisClient.class);
		//利用jedisClient对象操作redis
		jedisClient.set("key", "value");
		System.out.println(jedisClient.get("key"));
	}
}
