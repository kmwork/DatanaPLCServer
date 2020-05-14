package ru.datana.steel.camel.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.datana.steel.camel.config.AppVersion;
import ru.datana.steel.camel.config.RestSpringConfig;
import ru.datana.steel.camel.config.SpringConfig;
import ru.datana.steel.camel.model.json.meta.JsonMetaRootController;
import ru.datana.steel.camel.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.camel.model.json.response.JsonRootSensorResponse;
import ru.datana.steel.camel.model.json.response.JsonSensorResponse;
import ru.datana.steel.camel.s7controller.S7GithubExecutor;
import ru.datana.steel.camel.util.AppException;
import ru.datana.steel.camel.util.DatanaJsonHelper;
import ru.datana.steel.camel.util.JsonParserUtil;

import java.util.List;

/**
 * Рест сервера наружу
 */
@Component
@Slf4j
public class S7RestApiImpl implements S7RestApi {
    @Autowired
    private SpringConfig springConfig;

    @Autowired
    private RestSpringConfig restSpringConfig;

    @Autowired
    private ApplicationContext context;

    @Override
    public String getVersion() {
        return AppVersion.getDatanaAppVersion();
    }


    @Override
    public String getData(@NonNull @RequestBody JsonRootSensorRequest rootJson) throws AppException {
        JsonParserUtil parserUtil = JsonParserUtil.getInstance();
        JsonRootSensorResponse result;
        S7GithubExecutor s7Executor = null;
        try {
            JsonMetaRootController jsonMeta = parserUtil.loadJsonMetaRootController();
            s7Executor = context.getBean(S7GithubExecutor.class);
            s7Executor.init(jsonMeta);
            result = s7Executor.run(rootJson);
        } catch (Exception ex) {
            log.error("Ошибка в программе: ", ex);
            result = new JsonRootSensorResponse();
            List<JsonSensorResponse> responseList = DatanaJsonHelper.getInstance().createJsonRequestWithErrorGlobal(rootJson, ex);
            result.setResponse(responseList);
        } finally {
            if (s7Executor != null)
                s7Executor.close();

        }
        return restSpringConfig.toJsonFromObject("[Server: Ответ] ", result);
    }

}
