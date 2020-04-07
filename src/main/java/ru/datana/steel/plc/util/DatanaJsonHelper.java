package ru.datana.steel.plc.util;

import lombok.Getter;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.request.JsonSensorSingleRequest;
import ru.datana.steel.plc.model.json.response.JsonSensorError;
import ru.datana.steel.plc.model.json.response.JsonSensorResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитый класс по работе с JSON`
 */
public class DatanaJsonHelper {

    @Getter
    private final static DatanaJsonHelper instance = new DatanaJsonHelper();

    /**
     * Счетчик ответов для формирование ID ответов
     */
    private int responseCount = 0;

    private DatanaJsonHelper() {

    }


    /**
     * Генератор ID
     *
     * @param prefix
     * @return
     */
    public String genResponseId(String prefix) {
        responseCount++;
        return "Response[" + prefix + "]:" + System.nanoTime() + ":Res-Index:" + responseCount;
    }

    /**
     * Сформировать JSON по ошибке
     *
     * @param request JSON-запрос по которому произошла ошибка
     * @param e       объект ошибки (Exception)
     * @return Json объект для отдачи в REST ответ
     */
    public JsonSensorResponse createJsonRequestWithError(JsonSensorSingleRequest request, Exception e) {
        JsonSensorResponse jsonResult = createJsonRequest(null, AppConst.JSON_ERROR_CODE);
        JsonSensorError jsonError = new JsonSensorError();
        if (e instanceof AppException) {
            AppException appEx = (AppException) e;
            jsonError.setMsg(appEx.getMsg() + ": " + appEx.getMainEx().getMessage());
            jsonError.setStrArgs(appEx.getStrArgs());
            jsonError.setTypeCode(appEx.getType().getCodeError());
        } else {
            jsonError.setStrArgs("request.ControllerId = " + request.getControllerId());
            jsonError.setMsg("Ошибка при работе S7 контроллера: " + e.getMessage());
            jsonError.setTypeCode(TypeException.SYSTEM_ERROR.getCodeError());
        }
        List<JsonSensorError> errors = new ArrayList<>();
        errors.add(jsonError);
        jsonResult.setErrors(errors);
        return jsonResult;
    }

    /**
     * Сформировать Json с данными под запрос
     *
     * @param value  значение датчика - цель запроса
     * @param status статус запроса (1 - ок, 0 - ошибка)
     * @return json ответа
     */
    public JsonSensorResponse createJsonRequest(BigDecimal value, int status) {
        JsonSensorResponse response = new JsonSensorResponse();
        response.setControllerDatetime(getCurrentTime());

        if (value != null)
            response.setData(value.toString());

        response.setId(genResponseId(AppConst.JSON_PREFIX_SENSOR));
        response.setStatus(status);
        return response;
    }

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

}
