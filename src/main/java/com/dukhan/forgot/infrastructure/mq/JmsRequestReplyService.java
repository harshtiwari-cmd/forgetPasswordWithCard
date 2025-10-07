//package com.dukhan.forgot.infrastructure.mq;
//
//
//import com.dukhan.forgot.config.AppProperties;
//import com.dukhan.forgot.config.MqProperties;
//import com.dukhan.forgot.infrastructure.common.exception.ApiException;
//import org.springframework.http.HttpStatus;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.TextMessage;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class JmsRequestReplyService {
//
//    private final JmsTemplate jmsTemplate;
//    private final AppProperties appProperties;
//    private final MqProperties mqProperties;
//    private final Map<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();
//
//    public JmsRequestReplyService(JmsTemplate jmsTemplate, AppProperties appProperties, MqProperties mqProperties) {
//        this.jmsTemplate = jmsTemplate;
//        this.appProperties = appProperties;
//        this.mqProperties = mqProperties;
//    }
//
//    public String sendRequestAndWaitForReply(String correlationId, String xmlMessage) {
//        return sendRequestAndWaitForReply(correlationId, xmlMessage,
//            mqProperties.getQueue().getSendSmsRequest(),
//            mqProperties.getQueue().getSendSmsReply());
//    }
//
//    public String sendRequestAndWaitForReply(String correlationId, String xmlMessage, String requestQueue, String replyQueue) {
//        try {
//            CompletableFuture<String> future = new CompletableFuture<>();
//            pendingRequests.put(correlationId, future);
//
//            jmsTemplate.send(requestQueue, session -> {
//                TextMessage message = session.createTextMessage(xmlMessage);
//                try {
//                    message.setJMSCorrelationID(correlationId);
//                } catch (JMSException e) {
//                    throw new RuntimeException(e);
//                }
//                message.setJMSReplyTo(session.createQueue(replyQueue));
//                return message;
//            });
//
//            return future.get(mqProperties.getRequestTimeout(), TimeUnit.MILLISECONDS);
//        } catch (Exception e) {
//            pendingRequests.remove(correlationId);
//            throw new ApiException(HttpStatus.BAD_GATEWAY, appProperties.getError().getMqConnection() + ": " + e.getMessage());
//        }
//    }
//
//    @JmsListener(destination = "${ibm.mq.queue.sendSms.reply}")
//    public void handleReply(Message message) throws JMSException {
//        if (message instanceof TextMessage textMessage) {
//            String correlationId = message.getJMSCorrelationID();
//            if (correlationId != null) {
//                CompletableFuture<String> future = pendingRequests.remove(correlationId);
//                if (future != null) {
//                    future.complete(textMessage.getText());
//                }
//            }
//        }
//    }
//}
