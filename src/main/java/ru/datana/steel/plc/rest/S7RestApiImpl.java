package ru.datana.steel.plc.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.SpringConfig;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.model.json.response.JsonRootSensorResponse;
import ru.datana.steel.plc.model.json.response.JsonSensorResponse;
import ru.datana.steel.plc.s7controller.S7GithubExecutor;
import ru.datana.steel.plc.util.AppException;
import ru.datana.steel.plc.util.DatanaJsonHelper;
import ru.datana.steel.plc.util.JsonParserUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@Profile(AppConst.SERVER_PROFILE)
public class S7RestApiImpl implements S7RestApi {
    @Autowired
    SpringConfig springConfig;
    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, path = "/rest/getVersion", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @Override
    public String getVersion() {
        return springConfig.getAppVersion();
    }


    @PostConstruct
    protected void init() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Override
    public JsonRootSensorResponse getData(@RequestBody JsonRootSensorRequest fromJson) {
        JsonParserUtil parserUtil = JsonParserUtil.getInstance();
        JsonRootSensorResponse result;
        try (S7GithubExecutor s7 = new S7GithubExecutor()) {
            JsonMetaRootController jsonMeta = parserUtil.loadJsonMetaRootController();
            s7.init(jsonMeta);
            result = s7.run(fromJson);
            log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        } catch (AppException | JsonProcessingException ex) {
            log.error("Ошибка в программе: ", ex);
            result = new JsonRootSensorResponse();
            JsonSensorResponse jsonError = DatanaJsonHelper.getInstance().createJsonRequestWithError(fromJson.getRequest().get(0), ex);
            List<JsonSensorResponse> responseList = new ArrayList<>();
            responseList.add(jsonError);
            result.setResponse(responseList);
        }
        return result;
    }
}
