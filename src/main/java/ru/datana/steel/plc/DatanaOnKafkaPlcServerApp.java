//package ru.datana.steel.plc;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import ru.datana.steel.plc.config.AppConst;
//import ru.datana.steel.plc.kafka.DanataPlcServerKafkaMessageProducer;
//import ru.datana.steel.plc.kafka.DatanaKafkaListener;
//import ru.datana.steel.plc.util.DatanaJsonHelper;
//import ru.datana.steel.plc.util.ExtSpringProfileUtil;
//@EnableWebMvc
//@SpringBootApplication
//@Slf4j
//public class DatanaOnKafkaPlcServerApp {
//
//    public static void main(String[] args) throws Exception {
//        ExtSpringProfileUtil.extConfigure();
//        ConfigurableApplicationContext context = SpringApplication.run(DatanaOnKafkaPlcServerApp.class, args);
//
//        DanataPlcServerKafkaMessageProducer producer = context.getBean(DanataPlcServerKafkaMessageProducer.class);
//        DatanaKafkaListener listener = context.getBean(DatanaKafkaListener.class);
//
//        DatanaJsonHelper jsonHelper = DatanaJsonHelper.getInstance();
//        producer.sendMessage(jsonHelper.genRequestId(AppConst.JSON_PREFIX_META_INFO), "Hello, World!");
//
//        for (int i = 0; i < 5; i++) {
//            producer.sendMessage(jsonHelper.genRequestId(AppConst.JSON_PREFIX_META_INFO), "Hello To Partioned Topic!");
//        }
//        context.close();
//    }
//
//
//}
