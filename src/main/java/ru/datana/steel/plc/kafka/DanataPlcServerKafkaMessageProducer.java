package ru.datana.steel.plc.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.DatanaCommonKafkaConfig;

@Slf4j
@Component
public class DanataPlcServerKafkaMessageProducer {

    private final static String PREFIX_LOG = "[Kafka:Server:Producer] ";
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private DatanaCommonKafkaConfig kafkaConfig;

    public void sendMessage(String requestId, String jsonMessage) {
        log.info(PREFIX_LOG + "Отпрака сообщение в кафку: requestId = {}, jsonMessage = {}", requestId, jsonMessage);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(kafkaConfig.getMetaInfoTopic(), requestId, jsonMessage);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info(PREFIX_LOG + "Послал сообщение в кафку: requestId = {}, результат = {}", requestId, result);
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error(AppConst.ERROR_LOG_PREFIX + "Ошибка отправки: requestId = {}", requestId, ex);
            }
        });
    }


}
