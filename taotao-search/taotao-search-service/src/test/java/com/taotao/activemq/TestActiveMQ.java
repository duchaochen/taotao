package com.taotao.activemq;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

public class TestActiveMQ {
    @Test
    public void testActiveMQ() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");

        System.in.read();
    }
}
