//package com.dukhan.forgot.config;
//
//import com.ibm.mq.jms.MQConnectionFactory;
//import com.ibm.msg.client.wmq.WMQConstants;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jms.connection.CachingConnectionFactory;
//import org.springframework.jms.core.JmsTemplate;
//
//import javax.jms.ConnectionFactory;
//
//@Configuration
//@EnableConfigurationProperties(MqProperties.class)
//public class MqConfig {
//
//    private final MqProperties mqProperties;
//
//    public MqConfig(MqProperties mqProperties) {
//        this.mqProperties = mqProperties;
//    }
//
//    @Bean
//    public ConnectionFactory mqConnectionFactory() throws Exception {
//        MQConnectionFactory factory = new MQConnectionFactory();
//        factory.setHostName(mqProperties.getHost());
//        factory.setPort(mqProperties.getPort());
//        factory.setChannel(mqProperties.getChannel());
//        factory.setQueueManager(mqProperties.getQueueManager());
//        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
//
//        // Set authentication if provided
//        if (mqProperties.getUser() != null && !mqProperties.getUser().isEmpty()) {
//            factory.setStringProperty(WMQConstants.USERID, mqProperties.getUser());
//        }
//        if (mqProperties.getPassword() != null && !mqProperties.getPassword().isEmpty()) {
//            factory.setStringProperty(WMQConstants.PASSWORD, mqProperties.getPassword());
//        }
//
//        // Create a CachingConnectionFactory that wraps the IBM MQ ConnectionFactory
//        CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
//        cachingFactory.setTargetConnectionFactory((ConnectionFactory) factory);
//        cachingFactory.setSessionCacheSize(10);
//        cachingFactory.setReconnectOnException(true);
//
//        return cachingFactory;
//    }
//
//    @Bean
//    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
//        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
//        jmsTemplate.setExplicitQosEnabled(true);
//        jmsTemplate.setDeliveryPersistent(true);
//        return jmsTemplate;
//    }
//}
//
//
//
