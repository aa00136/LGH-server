package com.lgh.redis;

import java.io.IOException;
import java.util.HashSet;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisDemo {
	public static void main(String[] args){
		HashSet<HostAndPort> nodes = new HashSet<HostAndPort>();
        nodes.add(new HostAndPort("10.21.32.113", 7001));
        nodes.add(new HostAndPort("10.21.32.113", 7002));
        nodes.add(new HostAndPort("10.21.32.113", 7003));
        nodes.add(new HostAndPort("10.21.32.113", 7004));
        nodes.add(new HostAndPort("10.21.32.113", 7005));
        nodes.add(new HostAndPort("10.21.32.113", 7006));
        JedisCluster cluster = new JedisCluster(nodes);
        //cluster.set("test2", "hello_world2");
        //for(int i=0;i<1000;i++){
        	//cluster.lpush("i", "i"+i);
        //}
        System.out.println("---:"+cluster.llen("i"));
        //关闭连接
        try {
			cluster.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
