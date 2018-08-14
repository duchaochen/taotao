package com.taotao.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpringRedis {
    @Test
    public void test01() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
        JedisClientPool jedisClientPool = (JedisClientPool) context.getBean("jedisClientPool");
        jedisClientPool.set("key1","网吧");
        System.out.println(jedisClientPool.get("key1"));
    }

    /**
     * 集群
     */
    @Test
    public void test02() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
        JedisClientCluster jedisClientPool = (JedisClientCluster) context.getBean("jedisClientCluster");
        jedisClientPool.set("key1","网吧111");
        System.out.println(jedisClientPool.get("key1"));
    }
}
