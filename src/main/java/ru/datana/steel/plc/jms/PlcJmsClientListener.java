package ru.datana.steel.plc.jms;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.datana.steel.plc.config.AppConst;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Сервис по JMS - точка входа в сервис по Apache ActiveMQ
 */
@Component("plcJmsClientListener")
@Slf4j
@Profile(AppConst.CLIENT_PROFILE)
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

    private AtomicInteger counter = new AtomicInteger(0);

    private static AtomicLong WAITING_COUNT = new AtomicLong(0);

    public static void initWaitingCounter(long count) {
        WAITING_COUNT.set(count);
    }

    public static long getWaitingCounter() {
        return WAITING_COUNT.get();
    }

    @SneakyThrows
    @Override
    public void onMessage(@NonNull Message message) {
        int indexMsg = counter.incrementAndGet();
        String prefix = PREFIX_LOG + "[onMessage, index = " + indexMsg + "] ";
        log.debug(prefix + "Пришло сообщение от сервера");
        try {
            if (message instanceof TextMessage) {

                if (log.isTraceEnabled()) {
                    String msg = ((TextMessage) message).getText();
                    log.trace(prefix + "input message = " + msg);
                } else log.info(prefix + "Пришло сообщение от сервера");
                counter.incrementAndGet();
            }
        } finally {
            WAITING_COUNT.decrementAndGet();
        }
    }


}
