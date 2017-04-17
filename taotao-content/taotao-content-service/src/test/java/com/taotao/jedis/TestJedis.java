package com.taotao.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import sun.tools.tree.Node;

public class TestJedis {
	
	
	@Test
	public void testJedis() throws Exception{
		//创建一个jedis对象，指定服务的ip的端口号
		Jedis jedis=new Jedis("127.0.0.1",6379);
		//直接操作数据库
		//jedis.set("jedis-key", "1234");
		String result=jedis.get("jedis-key");
		System.out.println(result);
		//关闭jedis
		jedis.close();
	}
	@Test
	public void testJedisPoll() throws Exception{
		//创建一个数据库连接池对象(单例)，需要指定服务的ip和端口号
		JedisPool jedisPoll=new JedisPool("127.0.0.1", 6379);
		//从连接池中获得连接
		Jedis jedis=jedisPoll.getResource();
		//使用Jedis操作数据库（方法级别使用）
		String result=jedis.get("jedis-key");
		System.out.println(result);
		//一定要关闭jedis连接
		jedis.close();
		//系统关闭前，关闭连接池
		jedisPoll.close();
	}
	@Test
	public void testJedisCluster() throws Exception{
		//创建一个JedisCluster对象，构造参数Set类型，集合中每个元素是HostAndPort类型
		Set<HostAndPort> nodes=new HashSet<>();
		nodes.add(new HostAndPort("127.0.0.1", 7001));
		nodes.add(new HostAndPort("127.0.0.1", 7002));
		nodes.add(new HostAndPort("127.0.0.1", 7003));
		nodes.add(new HostAndPort("127.0.0.1", 7004));
		nodes.add(new HostAndPort("127.0.0.1", 7005));
		nodes.add(new HostAndPort("127.0.0.1", 7006));
		JedisCluster jedisCluster=new JedisCluster(nodes);
		//直接使用JedisCluster操作redis，自带连接池，JedisCluster可以是单例的
		//jedisCluster.set("cluster-test", "hello,jedisCluster");
		String res=jedisCluster.get("cluster-test");
		System.out.println(res);
		//系统关闭前关闭JedisCluster
		//jedisCluster.close();
	}
	
}
