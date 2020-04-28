package ru.datana.steel.plc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import ru.datana.steel.plc.jms.PlcJmsListener;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

/**
 * Для теста: ActiveMQ коннект на базе бинов
 */
@Configuration
@EnableJms
@Slf4j
public class MesActiveMqSpringConfig {


    @Autowired
    protected JmsProperties jmsProperties;

    @Autowired
    protected MessageListener plcJmsListener;

    @Bean
    protected JmsTemplate jmsRequestTemplate(@Qualifier("activeMqJMSConnectionFactory") ConnectionFactory connectionFactory,
                                             @Qualifier("activeMqRequestDestination") ActiveMQQueue requestQueue) {
        JmsTemplate template = new JmsTemplate();

        template.setConnectionFactory(connectionFactory);
        template.setDefaultDestination(requestQueue);
        return template;
    }


    @Bean
    protected ConnectionFactory activeMqJMSConnectionFactory() {
        return new ActiveMQConnectionFactory(jmsProperties.getBrokerUrl());
    }

    @Bean
    protected MessageListenerContainer listenerContainer(@Qualifier("activeMqJMSConnectionFactory") ConnectionFactory connectionFactory,
                                                         @Qualifier("activeMqRequestDestination") ActiveMQQueue requestQueue) {
        DefaultMessageListenerContainer listenerContainer = new DefaultMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.setDestination(requestQueue);
        listenerContainer.setMessageListener(plcJmsListener);
        return listenerContainer;
    }

    @Bean
    protected ActiveMQQueue activeMqRequestDestination() {
        return new ActiveMQQueue(jmsProperties.getRequestQueue());
    }


    @Bean
    protected ActiveMQQueue activeMqResponseDestination() {
        return new ActiveMQQueue(jmsProperties.getResponseQueue());
    }

    @Bean
    protected MessageListener plcJmsReceiver() {
        return new PlcJmsListener();
    }

}
