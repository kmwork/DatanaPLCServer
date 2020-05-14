package ru.datana.steel.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.datana.steel.camel.camel.DatanaRouteBuilder;
import ru.datana.steel.camel.config.AppVersion;
import ru.datana.steel.camel.util.ExtSpringProfileUtil;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class PlcForKafkaApp {

    @Autowired
    private CamelContext camelContext;

    @PostConstruct
    private void init() throws Exception {
        log.info("[App:Init] version = " + AppVersion.getDatanaAppVersion());
        camelContext.addRoutes(new DatanaRouteBuilder());
    }

    public static void main(String[] args) {
        ExtSpringProfileUtil.extConfigure();
        SpringApplication app = new SpringApplication(PlcForKafkaApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
