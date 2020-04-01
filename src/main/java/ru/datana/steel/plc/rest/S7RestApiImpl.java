package ru.datana.steel.plc.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.datana.steel.plc.config.SpringConfig;

@RestController
@Slf4j
public class S7RestApiImpl implements S7RestApi {
    @Autowired
    SpringConfig springConfig;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/getVersion", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getVersion() {
        return springConfig.getAppVersion();
    }
}
