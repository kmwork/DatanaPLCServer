package ru.datana.steel.plc.jms;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.RestSpringConfig;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.rest.S7RestApi;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Сервис по JMS - точка входа в сервис по Apache ActiveMQ
 */
@Component("plcJmsListener")
@Slf4j
public class PlcJmsListener implements MessageListener {

    private final static String PREFIX_LOG = "[JMS:Listener] ";

    @Autowired
    private S7RestApi s7RestApi;

    @Autowired
    private RestSpringConfig restSpringConfig;

    @Autowired
    private PlcJmsProducer jmsProducer;

    @PostConstruct
    protected void postConstruct() {
        log.info(PREFIX_LOG + "Запуск JMS-сервиса.");
    }

    @PreDestroy
    protected void preDestroy() {
        log.info(PREFIX_LOG + "Остановка JMS-сервиса.");
    }

    private AtomicInteger counter = new AtomicInteger(0);
    @Override
    public void onMessage(@NonNull Message message) {

        String prefix = PREFIX_LOG + "[onMessage] ";
        String msg = null;
        String jmsDestination = null;
        String errorMsg;
        try {
            jmsDestination = message.getJMSDestination().toString();

            if (message instanceof TextMessage) {
                msg = ((TextMessage) message).getText();
                log.info(prefix + "input message = " + msg);
            } else {
                errorMsg = "WARN: not text message, type message : " + message.getJMSType();
                log.warn(AppConst.ERROR_LOG_PREFIX + "Не валидный JMS: " + errorMsg);
            }

            //JSON file to Java object
            JsonRootSensorRequest jsonRootRequest = restSpringConfig.parseValue(msg, JsonRootSensorRequest.class);
            if (log.isTraceEnabled())
                log.trace("[JSON-Parser] jsonRootRequest = " + jsonRootRequest);

            log.info(prefix + "input message, index =" + counter.incrementAndGet());
            String result = s7RestApi.getData(jsonRootRequest);
            jmsProducer.sendOnSuccess(result);
        } catch (Exception e) {
            log.error(AppConst.ERROR_LOG_PREFIX + "Системная ошибка jmsDestination = {}, ,msg = {}, в классе = {}", jmsDestination, msg, getClass().getSimpleName());
            log.error(AppConst.ERROR_LOG_PREFIX, e);
        }

    }


}
