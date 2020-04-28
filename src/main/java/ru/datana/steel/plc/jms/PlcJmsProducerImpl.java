package ru.datana.steel.plc.jms;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * Продюсер для отправки JMS сообщений
 */
@Slf4j
@Service
public class PlcJmsProducerImpl implements PlcJmsProducer {
    private final static String PREFIX_LOG = "[JMS:Producer] ";
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Очередь для ошибок
     */
    @Value("${datana.activemq.responseQueueOnError}")
    private String queueOnError;

    /**
     * Очередь для подтверждения успеха
     */
    @Value("${datana.activemq.responseQueue}")
    private String queueForSuccess;

    @Override
    public void sendOnError(@NonNull String jmsAsStringMsg) {
        send("Method:Отправка ошибок", queueOnError, jmsAsStringMsg);
    }

    @Override
    public void send(@NonNull String methodName, @NonNull String queue, @NonNull String xmlAsStringMsg) {
        String prefix = PREFIX_LOG + "[Queue:" + queue + "] ";
        log.debug(prefix + " вызов метода " + methodName);
        jmsTemplate.convertAndSend(queue, xmlAsStringMsg);
        if (log.isTraceEnabled())
            log.trace(prefix + "Отправлен в очередь = {}, xmlAsStringMsg = {}", queue, xmlAsStringMsg);
        else
            log.info("Отправлено сообщение в очередь " + queue);
    }

    @Override
    public void sendOnSuccess(@NonNull String jmsAsStringMsg) {
        send("Method:Отправка ответов при успехе", queueForSuccess, jmsAsStringMsg);
    }
}