package com.taota.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.io.IOException;

public class TestActinveMq {

    @Test
    public void testMq() throws JMSException {
        //1.创建一个连接工厂对象ConnectionFactory对象。需要指定mq服务的ip及端口
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.146:61616");
        //2.使用ConnectionFactory创建一个连接Connection对象
        Connection connection = connectionFactory.createConnection();
        //3.开启连接。调用Connection对象的start方法
        connection.start();
        /**
         * 4.使用Connection对象创建一个Session对象
         * 第一个参数是是否开启事务，一般不使用事务。保证数据的最终一致，可以使用消息队列实现。
         * 如果第一个参数为true，第二个参数自动忽略。如果不开启事务false，
         * 第二个参数为消息的应答模式。一般自动应答就可以。
         */
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        /**
         * 5.使用Session对象创建一个Destination对象，两种形式queue、topic。现在应该使用queue
         * 参数就是消息队列的名称
         */
        Queue queue = session.createQueue("test-queue");
        //6.使用Session对象创建一个Producer对象来发送消息
        MessageProducer producer = session.createProducer(queue);
        /**
         * 7.使用session创建一个TextMessage对象
         * TextMessage textMessage = new ActiveMQTextMessage();
         * textMessage.setText("hello activemq");
         */
        TextMessage textMessage = session.createTextMessage("hello activemq333");
        //8.发送消息
        producer.send(textMessage);
        //9.关闭资源
        producer.close();
        session.close();
        connection.close();
    }

    /**
     * 单个接收
     * @throws Exception
     */
    @Test
    public void testQueueConsumer() throws Exception {
        //创建一个连接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.146:61616");
        //使用连接工厂对象创建一个连接
        Connection connection = connectionFactory.createConnection();
        //开启连接
        connection.start();
        //使用连接对象创建一个Session对象
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //使用Session创建一个Destination，Destination应该和消息的发送端一致
        //创建一个消息队列目的地对象
        Queue queue = session.createQueue("test-queue");
        //使用Session创建一个Consumer对象
        MessageConsumer consumer = session.createConsumer(queue);
        //向Consumer对象中设置一个MessageListener对象，用来接收消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String text = textMessage.getText();
                        System.out.println(text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //系统等待接收消息
		/*while(true) {
			Thread.sleep(100);
		}*/
		System.in.read();
        //关闭资源
        consumer.close();
        session.close();
        connection.close();
    }

    /**
     * 发送方
     * @throws JMSException
     */
    @Test
    public void testTopicProducer() throws JMSException {
        //创建connetionFactory工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.146:61616");
        //创建connection链接对象
        Connection connection = connectionFactory.createConnection();
        //开启链接
        connection.start();;
        //创建session对象
        Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        //创建一个消息队列目的地对象
        Topic topic = session.createTopic("test-topic");
        //创建一个生产者
        MessageProducer producer = session.createProducer(topic);
        //创建消息
        TextMessage textMessage = session.createTextMessage("放屁");
        //发送
        producer.send(textMessage);
        //关闭连接
        producer.close();
        session.close();
        connection.close();
    }

    /**
     * 消费者
     */
    @Test
    public void testTopicCostom() throws JMSException, IOException {
        //创建一个connection工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.146:61616");
        //创建一个链接对象
        Connection connection = connectionFactory.createConnection();
        //打开链接
        connection.start();
        //创建session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建一个消息队列目的地对象
        Topic topic = session.createTopic("test-topic");
        //创建一个消费者对象
        MessageConsumer consumer = session.createConsumer(topic);
        //监听消息事件
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println(textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("第3个");
        //停顿主线程
        System.in.read();
        //关闭服务
        consumer.close();
        session.close();
        connection.close();
    }

    @Test
    public void testSpringAndMq() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        Destination destination = (Destination) context.getBean("test-queue");
        jmsTemplate.send(destination,new MessageCreator(){
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage("spring整合消息发送11111111111111");
                return textMessage;
            }
        });
    }
}
