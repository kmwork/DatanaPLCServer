package ru.datana.steel.plc.jms;

import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Продюсер для отправки JMS сообщений
 */
@Component("jmsProducer")
public interface PlcJmsProducer {
    void sendOnError(@NonNull String msg);

    void sendOnSuccess(@NonNull String msg);

    void send(@NonNull String methodName, @NonNull String queue, @NonNull String jsonAsStringMsg);
}
