package ru.datana.steel.plc.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.datana.steel.plc.config.AppConst;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@Profile(AppConst.SERVER_PROFILE)
public class S7RestApiImpl implements S7RestApi {
    @Autowired
    private SpringConfig springConfig;

    @Autowired
    private RestSpringConfig restSpringConfig;


    @RequestMapping(method = RequestMethod.GET, path = "/rest/getVersion", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @Override
    public String getVersion() {
        return springConfig.getAppVersion();
    }


    @RequestMapping(method = RequestMethod.POST, path = "/rest/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Override
    public String getData(@RequestBody JsonRootSensorRequest rootJson) throws AppException {
        JsonParserUtil parserUtil = JsonParserUtil.getInstance();
        JsonRootSensorResponse result;
        try (S7GithubExecutor s7 = new S7GithubExecutor()) {
            JsonMetaRootController jsonMeta = parserUtil.loadJsonMetaRootController();
            s7.init(jsonMeta);
            result = s7.run(rootJson);
        } catch (AppException ex) {
            log.error("Ошибка в программе: ", ex);
            result = new JsonRootSensorResponse();
            JsonSensorResponse jsonError = DatanaJsonHelper.getInstance().createJsonRequestWithError(rootJson, null, ex);
            List<JsonSensorResponse> responseList = new ArrayList<>();
            responseList.add(jsonError);
            result.setResponse(responseList);
        }
        String strResult = restSpringConfig.toJsonFromObject("[Server: Ответ] ", result);
        return strResult;
    }
}
