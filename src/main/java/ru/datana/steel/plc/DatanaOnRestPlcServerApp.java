package ru.datana.steel.plc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;

@EnableWebMvc
@SpringBootApplication
@Slf4j
public class DatanaOnRestPlcServerApp {

    public static void main(String[] args) throws Exception {
        ExtSpringProfileUtil.extConfigure();
        ConfigurableApplicationContext context = SpringApplication.run(DatanaOnRestPlcServerApp.class, args);
    }


}
