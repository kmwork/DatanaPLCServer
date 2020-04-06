package ru.datana.steel.plc.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.datana.steel.plc.config.AppConst;

@FeignClient(url = "${datana.plc-server.url-server}", name = "plcServer")
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
@RequestMapping(path = "/rest")
public interface RestClientWebService {
    @GetMapping(value = "/getVersion", produces = MediaType.TEXT_PLAIN_VALUE)
    String getVersion();

    @PostMapping(value = "/getData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    String getData(@RequestBody String request);

}
