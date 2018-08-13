package com.taotao.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TestJedis {

    @Test
    public void test1(){
        Jedis jedis = new Jedis("192.168.25.138",6379);
        jedis.set("key1","张三");

        System.out.println(jedis.get("key1"));
        jedis.close();
    }
    @Test
    public void test2(){
        JedisPool jedisPool = new JedisPool("192.168.25.138",6379);
        Jedis jedis = jedisPool.getResource();
        jedis.set("key1","张三111");

        System.out.println(jedis.get("key1"));
        jedis.close();
        jedisPool.close();
    }


}
