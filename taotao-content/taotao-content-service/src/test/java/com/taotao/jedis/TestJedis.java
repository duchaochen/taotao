package com.taotao.jedis;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Set;

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

    /**
     * 集群
     */
    @Test
    public void test3(){
        Set<HostAndPort> set = new HashSet<>();
        set.add(new HostAndPort("192.168.25.145",7001));
        set.add(new HostAndPort("192.168.25.145",7002));
        set.add(new HostAndPort("192.168.25.145",7003));
        set.add(new HostAndPort("192.168.25.145",7004));
        set.add(new HostAndPort("192.168.25.145",7005));
        set.add(new HostAndPort("192.168.25.145",7006));

        JedisCluster jedisPool = new JedisCluster(set);
        jedisPool.set("key1","李四");
        System.out.println(jedisPool.get("key1"));
        jedisPool.close();
    }




}
