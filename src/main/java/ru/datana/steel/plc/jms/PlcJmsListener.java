package ru.datana.steel.plc.jms;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.util.JsonParserUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Сервис по JMS - точка входа в сервис по Apache ActiveMQ
 */
@Component("plcJmsListener")
@Slf4j
public class PlcJmsListener implements MessageListener {

    private final static String PREFIX_LOG = "[JMS:Listener] ";

    private JsonParserUtil parser = JsonParserUtil.getInstance();

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
            JsonRootSensorRequest jsonRootRequest = parser..readValue(msg, JsonRootSensorRequest.class);
            log.info("[JSON-Parser] jsonRootRequest = " + jsonRootRequest);

            log.info(prefix + "input message = " + msg);
            boolean isValid = StringUtils.isEmpty(errorMsg);
            String id = isValid ? xpathUtil.getIdByRequest(msg) : null;
            if (StringUtils.isNotEmpty(id)) {
                log.info(PREFIX_LOG + "ID полученного сообщения = " + id);
                String answer = callDbService.dbSave(msg);
                String status = xpathUtil.getStatusOfResponse(answer);
                if (AppConst.SUCCESS_STATUS_OF_PG_SAVE.equalsIgnoreCase(status)) {
                    jmsProducer.sendOnSuccess(answer);
                    log.info(AppConst.SUCCESS_LOG_PREFIX + "Сообщение обработано c Id = " + id);
                } else {
                    log.warn(AppConst.ERROR_LOG_PREFIX + "Ошибка в хранимке, неуспешный статус = " + status);
                    jmsProducer.sendOnError(answer);
                }
            } else {
                jmsProducer.sendOnError(errorMsg);
            }
        } catch (Exception e) {
            log.error(AppConst.ERROR_LOG_PREFIX + "Системная ошибка jmsDestination = {}, ,msg = {}, в классе = {}", jmsDestination, msg, getClass().getSimpleName());
            log.error(AppConst.ERROR_LOG_PREFIX, e);
        }

    }


}
