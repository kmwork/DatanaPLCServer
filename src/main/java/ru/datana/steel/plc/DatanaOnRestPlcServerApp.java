package ru.datana.steel.plc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;

@EnableWebMvc
@SpringBootApplication
@Slf4j
@Profile(AppConst.SERVER_PROFILE)
public class DatanaOnRestPlcServerApp {

    public static void main(String[] args) throws Exception {
        ExtSpringProfileUtil.extConfigure(AppConst.SERVER_PROFILE, AppConst.EXT_SERVER_YAML);
        ConfigurableApplicationContext context = SpringApplication.run(DatanaOnRestPlcServerApp.class, args);
    }


}
