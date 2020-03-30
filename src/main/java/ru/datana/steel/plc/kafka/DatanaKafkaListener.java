package ru.datana.steel.plc.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DatanaKafkaListener {


    @KafkaListener(topics = "datanaCommonKafkaConfig.metaInfoTopic", groupId = "datanaCommonKafkaConfig.kafkaConsumerMetaGroupId", containerFactory = "metaKafkaListenerContainerFactory")
    public void listenGroupMeta(String message) {
        log.info("Received Message in group 'meta': " + message);
    }
}
