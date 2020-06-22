package ru.datana.steel.plc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;

/**
 * Шлюз - сервисы RESTfull WebServices
 * Тех задание: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Server
 */
@EnableWebMvc
@EnableAsync
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@Slf4j
@Profile(AppConst.SERVER_PROFILE)
public class DatanaOnRestPlcServerApp {

    public static void main(String[] args) {
        log.debug("Run app");
        ExtSpringProfileUtil.extConfigure(AppConst.SERVER_PROFILE, AppConst.EXT_SERVER_YAML);
        SpringApplication.run(DatanaOnRestPlcServerApp.class, args);
    }


}
