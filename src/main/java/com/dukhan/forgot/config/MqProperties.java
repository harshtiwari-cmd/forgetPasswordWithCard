//package com.dukhan.forgot.config;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "ibm.mq")
//public class MqProperties {
//
//    private String host;
//    private int port;
//    private String channel;
//    private String queueManager;
//    private String user;
//    private String password;
//    private long requestTimeout;
//    private long correlationTimeout;
//
//    // Queue names
//    private QueueNames queue = new QueueNames();
//
//    public static class QueueNames {
//        private String sendSmsRequest;
//        private String sendSmsReply;
//
//        // Getters and setters
//        public String getSendSmsRequest() {
//            return sendSmsRequest;
//        }
//
//        public void setSendSmsRequest(String sendSmsRequest) {
//            this.sendSmsRequest = sendSmsRequest;
//        }
//
//        public String getSendSmsReply() {
//            return sendSmsReply;
//        }
//
//        public void setSendSmsReply(String sendSmsReply) {
//            this.sendSmsReply = sendSmsReply;
//        }
//    }
//
//    // Getters and setters
//    public String getHost() {
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public int getPort() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    public String getChannel() {
//        return channel;
//    }
//
//    public void setChannel(String channel) {
//        this.channel = channel;
//    }
//
//    public String getQueueManager() {
//        return queueManager;
//    }
//
//    public void setQueueManager(String queueManager) {
//        this.queueManager = queueManager;
//    }
//
//    public String getUser() {
//        return user;
//    }
//
//    public void setUser(String user) {
//        this.user = user;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public long getRequestTimeout() {
//        return requestTimeout;
//    }
//
//    public void setRequestTimeout(long requestTimeout) {
//        this.requestTimeout = requestTimeout;
//    }
//
//    public long getCorrelationTimeout() {
//        return correlationTimeout;
//    }
//
//    public void setCorrelationTimeout(long correlationTimeout) {
//        this.correlationTimeout = correlationTimeout;
//    }
//
//    public QueueNames getQueue() {
//        return queue;
//    }
//
//    public void setQueue(QueueNames queue) {
//        this.queue = queue;
//    }
//}
//
