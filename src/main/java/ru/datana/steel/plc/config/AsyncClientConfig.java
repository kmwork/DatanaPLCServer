package ru.datana.steel.plc.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public class AsyncClientConfig implements AsyncConfigurer {


    @Value("${datana.plc-client.tread-count-max}")
    @Getter
    private int threadCountMax;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCountMax);
        executor.setMaxPoolSize(128);
        executor.setQueueCapacity(threadCountMax);
        executor.setThreadNamePrefix("PlcExecutor-");
        executor.initialize();
        return executor;
    }
}