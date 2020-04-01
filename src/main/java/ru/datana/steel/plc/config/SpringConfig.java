package ru.datana.steel.plc.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;


@Configuration
@ConfigurationProperties(prefix = "datana.global")
@Slf4j
@EnableTransactionManagement
public class SpringConfig implements WebMvcConfigurer {

    @Getter
    @Setter
    protected String appVersion;

    @PostConstruct
    protected void postConstruct() {
        log.info("[SpringConfig] APP VERSION = " + appVersion);
    }

}