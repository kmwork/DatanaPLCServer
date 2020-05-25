package ru.datana.steel.plc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * Для теста: ActiveMQ коннект на базе бинов
 */
@Configuration
@EnableJms
@Slf4j
@Profile(AppConst.CLIENT_PROFILE)
public class ClientActiveMqSpringConfig {


    @Autowired
    protected JmsProperties jmsProperties;

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
    protected ActiveMQQueue activeMqRequestDestination() {
        return new ActiveMQQueue(jmsProperties.getRequestQueue());
    }


    @Bean
    protected ActiveMQQueue activeMqResponseDestination() {
        return new ActiveMQQueue(jmsProperties.getResponseQueue());
    }

}