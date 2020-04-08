package ru.datana.steel.plc.util;

import lombok.Getter;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
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
     * Сформировать JSON по ошибке
     *
     * @param rootRequest   произошла ошибка
     * @param singleRequest подзапрос по которому произошла ошибка
     * @param e             объект ошибки (Exception)
     * @return Json объект для отдачи в REST ответ
     */
    public JsonSensorResponse createJsonRequestWithError(JsonRootSensorRequest rootRequest, JsonSensorSingleRequest singleRequest, Exception e) {
        JsonSensorResponse jsonResult = createJsonRequest(null, AppConst.JSON_ERROR_CODE, rootRequest.getRequestId());
        JsonSensorError jsonError = new JsonSensorError();
        if (e instanceof AppException) {
            AppException appEx = (AppException) e;
            String eMsg = appEx.getMainEx() == null ? "<Нет вложенной ошибки>" : appEx.getMainEx().getMessage();
            jsonError.setMsg(appEx.getMsg() + ": " + eMsg);
            jsonError.setStrArgs(appEx.getStrArgs());
            jsonError.setTypeCode(appEx.getType().getCodeError());
        } else {
            String strController = singleRequest == null ? "<нет ControllerId>" : "ControllerId = " + singleRequest.getControllerId();
            jsonError.setStrArgs("rootRequestUUID = " + rootRequest.getRequestId() + ", " + strController);
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
    public JsonSensorResponse createJsonRequest(BigDecimal value, int status, String requestId) {
        JsonSensorResponse response = new JsonSensorResponse();

        //нет времени с контролера
        response.setControllerDatetime(null/*getCurrentTime()*/);

        if (value != null)
            response.setData(value.toString());

        response.setId(requestId);
        response.setStatus(status);
        return response;
    }

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

}
