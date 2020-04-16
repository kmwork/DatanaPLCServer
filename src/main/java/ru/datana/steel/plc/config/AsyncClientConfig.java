package ru.datana.steel.plc.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
@Slf4j
@EnableAsync
public class AsyncClientConfig implements AsyncConfigurer {
    private static final String PREFIX_LOG = "[Config:Async] ";

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
        log.debug(PREFIX_LOG + "настроен на " + threadCountMax + " потоков");
        return executor;
    }


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        log.debug(PREFIX_LOG + "Настройка обработчика ошибок для Async");
        return (throwable, method, objects)
                -> log.error(AppConst.ERROR_LOG_PREFIX + PREFIX_LOG + "Ошибки асинхронности", throwable);
    }
}