package com.taotao.activeMq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestActiveMQ {

	@Test
	public void testQueueProducer() throws Exception{
	//第一步：创建ConnectionFactory对象，需要指定服务器ip以及端口号
		//brokerURL服务器的ip及端口号
		ConnectionFactory connectionFactory=new 
				ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
	//第二步：使用ConnectionFactory对象创建一个Connection
	Connection connection=connectionFactory.createConnection();
	//第三步：开启连接，调用Connection对象的start方法
	connection.start();
	//第四步：使用Connection对象创建一个Session对象
		//第一个参数：是否开启食物。true：开启事务，第二个参数忽略
		//第二个参数，当地一个参数为false时，才有意义。消息的应答模式
			//1：自动应答  2:手动应答 一般是自动应答
	Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	//第五步：使用Session对象创建一个Destination对象（topic，queue），此处创建一个queue对象
		//参数：队列的名称
	Queue queue=session.createQueue("test-queue");
	//第六步：使用Session对象创建一个Producer对象
	MessageProducer producer=session.createProducer(queue);
	//第七步：创建一个Message对象，创建一个TextMessage对象
	TextMessage textMessage=session.createTextMessage("hello activeMQ");
	//第八步：使用Producer对象发送消息
	producer.send(textMessage);
	//第九步：关闭资源
	producer.close();
	session.close();
	connection.close();
	}
	
	
	@Test
	public void testQueueConsumer() throws Exception{
		//第一步：创建一个ConnectionFactory对象
		ConnectionFactory connectionFactory=new 
				ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
		//第二步：从ConnectionFactory对象中获得一个Connection对象
			Connection connection=connectionFactory.createConnection();
		//第三步：开启连接。调用Connection对象的start方法
			connection.start();
		//第四步：使用Connection对象创建一个Session对象
			Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//第五步：使用Session对象创建一个Destination对象。和发送端
				//保持一致queue，并且队列名称一致
			Queue queue=session.createQueue("test-queue");
		//第六步：使用session对象创建一个Consumer对象
			MessageConsumer consumer=session.createConsumer(queue);
		//第七步：接收消息
			consumer.setMessageListener(new MessageListener() {
				
				@Override
				public void onMessage(Message message) {
					try {
					TextMessage textMessage=(TextMessage) message;
					String text=null;
					//取消息内容
					text=textMessage.getText();
					//第八步：打印消息
					System.out.println(text);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			});
		//等待键盘输入,阻止程序结束，实时监测消息发布
			System.in.read();
		//第九步：关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
	
	@Test
	public void testTopicProducer() throws Exception{
		//第一步：创建ConnectionFactory对象，需要指定服务端ip和端口号
			//brokerURL服务器的ip及端口号
		ConnectionFactory connectionFactory=new 
				ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
		//第二步：使用ConnectionFactory对象创建一个Connection对象
		Connection connection=connectionFactory.createConnection();
		//第三步：开启连接，调用Connection对象的start方法
		connection.start();
		//第四步：使用Connection对象创建一个Session对象
			//第一个参数：是否开启事务 true：开启事务，第二个参数忽略
			//第二个参数：当地一个参数为false时，才有意义。消息应答模式
				//1:自动应答   2:手动应答 一般选择自动应答
		Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//第五步：使用session对象创建一个Destination对象（topic，queue）
			//参数：话题的名称
		Topic topic=session.createTopic("test-topic");
		//第六步：使用Session对象创建一个Producer对象
		MessageProducer producer=session.createProducer(topic);
		//第七步：创建一个Message对象，创建一个TextMessage对象
		TextMessage textMessage=session.createTextMessage("hellow activeMQ,test -topic");
		//第八步：使用Producer发送消息
		producer.send(textMessage);
		//第九步：关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	@Test
	public void testTopicConsumer() throws Exception{
		//第一步：创建一个ConnectionFactory对象
		ConnectionFactory connectionFactory=new 
				ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
		//第二步：从ConnectionFactory对象中获得一个Connection对象
		Connection connection=connectionFactory.createConnection();
		//第三步：开启连接。调用Connection对象的start方法
		connection.start();
		//第四步：使用Connection对象创建一个Session对象
		Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//第五步：使用session对象创建一个Destination对象，和发送端保持类型一致，名称一致，
		Topic topic=session.createTopic("test-topic");
		//第六步：使用session对象创建一个Consumer对象
		MessageConsumer consumer=session.createConsumer(topic);
		//第七步：接受消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				try {
					TextMessage textMessage=(TextMessage)message;
					String text=null;
					//取消息内容
					text=textMessage.getText();
					//第八步：打印消息
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		//等待键盘输入,阻止程序结束，实时监测消息发布
		System.in.read();
		//第九步：关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
}
