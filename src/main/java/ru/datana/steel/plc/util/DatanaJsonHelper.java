package ru.datana.steel.plc.util;

import lombok.Getter;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.model.json.request.JsonSensorDataVal;
import ru.datana.steel.plc.model.json.request.JsonSensorDatum;
import ru.datana.steel.plc.model.json.request.JsonSensorSingleRequest;
import ru.datana.steel.plc.model.json.response.JsonSensorError;
import ru.datana.steel.plc.model.json.response.JsonSensorResponse;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Утилитый класс по работе с JSON`
 */
public class DatanaJsonHelper {

    @Getter
    private final static DatanaJsonHelper instance = new DatanaJsonHelper();

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
    public List<JsonSensorResponse> createJsonRequestWithError(@NotNull JsonRootSensorRequest rootRequest, @NotNull JsonSensorSingleRequest singleRequest, @Null Set<Integer> successDataValIDs, @NotNull Exception e) {
        List<JsonSensorResponse> result = new ArrayList<>();
        for (JsonSensorDatum datum : singleRequest.getData()) {
            for (JsonSensorDataVal dataVal : datum.getDataVals()) {
                if (successDataValIDs != null && successDataValIDs.contains(dataVal.getId())) {
                    continue;
                }
                JsonSensorResponse jsonResult = createJsonRequestForData(null, AppConst.JSON_ERROR_CODE, dataVal);
                JsonSensorError jsonError = new JsonSensorError();
                if (e instanceof AppException) {
                    AppException appEx = (AppException) e;
                    String eMsg = appEx.getMainEx() == null ? "<Нет вложенной ошибки>" : appEx.getMainEx().getMessage();
                    jsonError.setMsg(appEx.getMsg() + ": " + eMsg);
                    jsonError.setStrArgs(appEx.getStrArgs());
                    jsonError.setTypeCode(appEx.getType().getCodeError());
                } else {
                    String strArgs = "rootRequestUUID = " + rootRequest.getRequestId() + "ControllerId = " + singleRequest.getControllerId();
                    jsonError.setStrArgs(strArgs);
                    jsonError.setMsg("Ошибка при работе S7 контроллера: " + e.getMessage());
                    jsonError.setTypeCode(TypeException.SYSTEM_ERROR.getCodeError());
                }
                List<JsonSensorError> errors = new ArrayList<>();
                errors.add(jsonError);
                jsonResult.setErrors(errors);
                result.add(jsonResult);
            }
        }
        return result;
    }

    /**
     * Сформировать Json с данными под запрос
     *
     * @param value  значение датчика - цель запроса
     * @param status статус запроса (1 - ок, 0 - ошибка)
     * @return json ответа
     */
    public JsonSensorResponse createJsonRequestForData(@Null BigDecimal value, int status, @NotNull JsonSensorDataVal dataVal) {
        JsonSensorResponse response = new JsonSensorResponse();

        //нет времени с контролера
        response.setControllerDatetime(null/*getCurrentTime()*/);

        if (value != null)
            response.setData(value.toString());

        response.setId(dataVal.getId());
        response.setStatus(status);
        return response;
    }

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    public List<JsonSensorResponse> createJsonRequestWithErrorGlobal(@NotNull JsonRootSensorRequest rootRequest, @NotNull AppException ex) {
        List<JsonSensorResponse> result = new ArrayList<>();
        for (JsonSensorSingleRequest singleRequest : rootRequest.getRequest()) {
            List<JsonSensorResponse> errors = createJsonRequestWithError(rootRequest, singleRequest, null, ex);
            result.addAll(errors);
        }
        return result;
    }
}
