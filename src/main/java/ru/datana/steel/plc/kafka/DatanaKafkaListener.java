package ru.datana.steel.plc.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DatanaKafkaListener {


    @KafkaListener(topics = "${kafka.topic}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
    public void listenGroupFoo(String message) {
        log.info("Received Messasge in group 'foo': " + message);
    }
}
