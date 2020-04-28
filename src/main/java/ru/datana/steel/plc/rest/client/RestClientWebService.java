package ru.datana.steel.plc.rest.client;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.datana.steel.plc.config.AppConst;

/**
 * Клиент для Restfull WebServices сервера
 */
@Profile(AppConst.CLIENT_PROFILE)
@RequestMapping(path = "/rest")
public interface RestClientWebService {
    /**
     * Получение версии сервера
     *
     * @return
     */
    String getVersion();

    /**
     * Получение данных по датчикам
     *
     * @param request
     * @return
     */
    String getData(@RequestBody String request);

}
