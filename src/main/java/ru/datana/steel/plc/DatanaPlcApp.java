package ru.datana.steel.plc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.datana.steel.plc.kafka.DanataPlcServerKafkaMessageProducer;
import ru.datana.steel.plc.kafka.DatanaKafkaListener;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;

@SpringBootApplication
@Slf4j
public class DatanaPlcApp {

    public static void main(String[] args) throws Exception {
        ExtSpringProfileUtil.extConfigure();
        ConfigurableApplicationContext context = SpringApplication.run(DatanaPlcApp.class, args);

        DanataPlcServerKafkaMessageProducer producer = context.getBean(DanataPlcServerKafkaMessageProducer.class);
        DatanaKafkaListener listener = context.getBean(DatanaKafkaListener.class);

        producer.sendMessage("Hello, World!");

        for (int i = 0; i < 5; i++) {
            producer.sendMessage("Hello To Partioned Topic!");
        }
        context.close();
    }


}
