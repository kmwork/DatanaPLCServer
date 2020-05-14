package ru.datana.steel.camel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.datana.steel.camel.config.RestSpringConfig;
import ru.datana.steel.camel.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.camel.util.AppException;

import java.util.concurrent.atomic.AtomicLong;

/**
 * PLC Proxy Client -- клиент для работы со шлюзом датчиков
 * Тех задание: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Client
 */
@Slf4j
@Component
public class ClientManager {
    @Autowired
    private RestSpringConfig restSpringConfig;

    private AtomicLong counter = new AtomicLong(0);

    @Autowired
    private S7RestApi s7RestApi;

    public String doRequest(JsonRootSensorRequest rootJson) throws AppException, InterruptedException {
        long step = counter.incrementAndGet();
        String prefixLog = "[Шаг: " + step + "] ";
        log.info(prefixLog);

        String toJson = s7RestApi.getData(rootJson);
        String resultFromJson = restSpringConfig.formatBeautyJson(prefixLog + " [Response] ", toJson);
        return resultFromJson;
    }


}

