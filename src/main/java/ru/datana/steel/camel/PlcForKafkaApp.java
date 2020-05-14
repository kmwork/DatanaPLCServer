package ru.datana.steel.camel;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.datana.steel.camel.util.ExtSpringProfileUtil;


@SpringBootApplication
public class PlcForKafkaApp {

    public static void main(String[] args) {
        ExtSpringProfileUtil.extConfigure();
        SpringApplication app = new SpringApplication(PlcForKafkaApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
