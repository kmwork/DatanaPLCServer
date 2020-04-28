package ru.datana.steel.plc.jms;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Сервис по JMS - точка входа в сервис по Apache ActiveMQ
 */
@Component("plcJmsClientListener")
@Slf4j
public class PlcJmsClientListener implements MessageListener {

    private final static String PREFIX_LOG = "[JMS:СlientListener] ";

    @PostConstruct
    protected void postConstruct() {
        log.info(PREFIX_LOG + "Запуск Клиент-JMS-сервиса.");
    }

    @PreDestroy
    protected void preDestroy() {
        log.info(PREFIX_LOG + "Остановка Клиент-JMS-сервиса.");
    }

    @Getter
    private AtomicInteger counter = new AtomicInteger(0);

    @SneakyThrows
    @Override
    public void onMessage(@NonNull Message message) {

        String prefix = PREFIX_LOG + "[onClientMessage] ";
        int indexMsg = counter.incrementAndGet();
        log.debug(prefix + "indexMsg = " + indexMsg);

        if (message instanceof TextMessage) {

            if (log.isTraceEnabled()) {
                String msg = ((TextMessage) message).getText();
                log.trace(prefix + "input message = " + msg);
            } else log.info(prefix + "Пришло сообщение от сервера");
            counter.incrementAndGet();
        }

    }


}
