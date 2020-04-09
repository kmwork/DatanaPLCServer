package ru.datana.steel.plc.rest;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.AppVersion;
import ru.datana.steel.plc.config.RestSpringConfig;
import ru.datana.steel.plc.config.SpringConfig;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.model.json.response.JsonRootSensorResponse;
import ru.datana.steel.plc.model.json.response.JsonSensorResponse;
import ru.datana.steel.plc.s7controller.S7GithubExecutor;
import ru.datana.steel.plc.util.AppException;
import ru.datana.steel.plc.util.DatanaJsonHelper;
import ru.datana.steel.plc.util.JsonParserUtil;

import java.util.List;

/**
 * Рест сервера наружу
 */
@RestController
@Slf4j
@Profile(AppConst.SERVER_PROFILE)
public class S7RestApiImpl implements S7RestApi {
    @Autowired
    private SpringConfig springConfig;

    @Autowired
    private RestSpringConfig restSpringConfig;

    @Autowired
    private ApplicationContext context;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/getVersion", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @Override
    public String getVersion() {
        return AppVersion.getDatanaAppVersion();
    }


    @RequestMapping(method = RequestMethod.POST, path = "/rest/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
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
        } catch (AppException ex) {
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
