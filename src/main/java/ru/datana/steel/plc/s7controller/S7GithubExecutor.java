package ru.datana.steel.plc.s7controller;

import com.github.s7connector.api.DaveArea;
import com.github.s7connector.impl.S7TCPConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.meta.Controller;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.model.json.request.JsonSensorDataVal;
import ru.datana.steel.plc.model.json.request.JsonSensorDatum;
import ru.datana.steel.plc.model.json.request.JsonSensorSingleRequest;
import ru.datana.steel.plc.model.json.response.JsonRootSensorResponse;
import ru.datana.steel.plc.model.json.response.JsonSensorResponse;
import ru.datana.steel.plc.util.AppException;
import ru.datana.steel.plc.util.DatanaJsonHelper;
import ru.datana.steel.plc.util.TimeUtil;
import ru.datana.steel.plc.util.TypeException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.Closeable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Движок от GitHub (упрощённое апи - подключил через зависимость мавен)
 * <p>
 * взято из s7connector
 * https://github.com/s7connector/s7connector
 */
@Slf4j
@Component("s7Executor")
@Profile(AppConst.SERVER_PROFILE)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class S7GithubExecutor implements Closeable {

    private String prefixLog = "[S7 Контроллер] ";

    private static final AtomicLong instanceCount = new AtomicLong(0);

    /**
     * Описание контроллеров по ID
     * ключ - наш ID в базе данных, значение - описание контроллера
     */
    private final Map<Integer, Controller> metaByControllerId = new HashMap<>();

    /**
     * Текущий коннект S7
     */
    private S7TCPConnection currentConnector = null;

    /**
     * Утилита по работе Json
     */
    private final DatanaJsonHelper jsonHelper = DatanaJsonHelper.getInstance();


    private long totalReadTimeNano = 0;
    private int totalDataSize = 0;

    @Value("${datana.global.experimental-s7-algorithm}")
    private boolean isExperimentalS7;


    @PostConstruct
    private void postConstructor() {
        long id = instanceCount.incrementAndGet();
        prefixLog += "[InstanceID: " + id + "] ";
        log.info(prefixLog + "Инициализация объекта-bean [S7GithubExecutor]");
    }

    /**
     * настройка сервиса чтения Siemens контроллеров
     *
     * @param controllerMeta
     */
    public void init(@NotNull JsonMetaRootController controllerMeta) {
        metaByControllerId.clear();
        for (Controller c : controllerMeta.getControllers()) {
            metaByControllerId.put(c.getId(), c);
        }
        log.info(prefixLog + " Прочитаны настройки для {} контролеров : {}", metaByControllerId.size(), metaByControllerId.keySet());
    }

    /**
     * Выполнить запрос клиента по снятию датчиков с нескольких контроллеров
     *
     * @param rootRequest все запросы на контроллеры для один сеанс сканирования датчиков
     * @return
     */
    public JsonRootSensorResponse run(@NotNull JsonRootSensorRequest rootRequest) throws ExecutionException, InterruptedException {

        totalReadTimeNano = 0;
        totalDataSize = 0;
        long startTime = System.nanoTime();
        LocalDateTime proxyTime = jsonHelper.getCurrentTime();
        List<JsonSensorResponse> jsonResponseList = new ArrayList<>();
        List<JsonSensorSingleRequest> list = rootRequest.getRequest();
        List<Future<List<JsonSensorResponse>>> futureList = new ArrayList<>();
        if (list != null)
            for (JsonSensorSingleRequest req : list) {
                if (metaByControllerId.containsKey(req.getControllerId())) {
                    Future<List<JsonSensorResponse>> future = doWorkRequest(rootRequest, req);
                    futureList.add(future);

                } else {
                    String strArg = "controllerId = " + req.getControllerId();
                    String msg = "Мета информация о контролере S7 = " + req.getControllerId() + " не найдена, есть информация только по ID = " + metaByControllerId.keySet();
                    Exception ex = new AppException(TypeException.S7CONTROLLER__INVALID_NOT_FOUND, msg, strArg, null);
                    log.warn(AppConst.ERROR_LOG_PREFIX + msg);
                    List<JsonSensorResponse> errorResponseList = jsonHelper.createJsonRequestWithError(rootRequest, req, null, ex);
                    jsonResponseList.addAll(errorResponseList);
                }
            }

        List<JsonSensorResponse> fList = waitFutureList(futureList);
        jsonResponseList.addAll(fList);
        JsonRootSensorResponse jsonResult = new JsonRootSensorResponse();
        jsonResult.setRequestDatetime(rootRequest.getRequestDatetime());
        jsonResult.setRequestDatetimeProxy(proxyTime);
        jsonResult.setResponseDatetime(jsonHelper.getCurrentTime());
        jsonResult.setRequestId(rootRequest.getRequestId());
        jsonResult.setTaskId(rootRequest.getTaskId());
        jsonResult.setResponse(jsonResponseList);
        long endTime = System.nanoTime();
        long deltaNano = endTime - startTime;
        log.info(AppConst.RESUME_LOG_PREFIX + "Затраченное время {}, из них время на чтение {}, вычитано {} байт", TimeUtil.formatTimeAsNano(deltaNano), TimeUtil.formatTimeAsNano(totalReadTimeNano), totalDataSize);
        return jsonResult;
    }

    private List<JsonSensorResponse> waitFutureList(List<Future<List<JsonSensorResponse>>> futureList) throws InterruptedException, ExecutionException {
        log.info(AppConst.RESUME_LOG_PREFIX + "[waitFutureList] В ожидании {} штук  future", futureList.size());
        List<JsonSensorResponse> result = new ArrayList<>();
        int index = 0;
        while (index < futureList.size()) {
            Future<List<JsonSensorResponse>> f = futureList.get(index);
            if (f.isDone()) {
                log.debug("[waitFutureList] isDone для index = " + index);
                index++;
                result.addAll(f.get());
            } else if (f.isCancelled()) {
                log.debug("[waitFutureList] isCancelled для index = " + index);
                index++;
            } else
                TimeUtil.doSleep(AppConst.SLEEP_FUTURE_MS, "[waitFutureList] В ожидании " + (futureList.size() - index) + " штук  future. Всего: " + futureList.size());
        }
        return result;
    }


    /**
     * Выполнить запрос на один контроллер
     *
     * @param request
     * @return
     */
    @Async
    protected Future<List<JsonSensorResponse>> doWorkRequest(@NotNull JsonRootSensorRequest rootRequest,
                                                             @NotNull JsonSensorSingleRequest request) {
        List<JsonSensorResponse> responseList = new ArrayList<>();
        try {

            for (JsonSensorDatum datum : request.getData()) {
                List<JsonSensorResponse> responsesByOneRequest = readBlockFromS7(rootRequest, request, datum);
                responseList.addAll(responsesByOneRequest);
            }
        } catch (AppException ex) {
            log.error(AppConst.ERROR_LOG_PREFIX + "Ошибка при чтении S7 для request = " + request, ex);
            List<JsonSensorResponse> responseError = jsonHelper.createJsonRequestWithError(rootRequest, request, null, ex);

            responseList.addAll(responseError);
        }

        return new AsyncResult<>(responseList);
    }


    /**
     * Пытаться прочитать массив байт на несколько датчиков сразу в рамках одного контроллера
     *
     * @param jsonRequest
     * @param intS7DBNumber
     * @param length
     * @param offset
     * @return
     * @throws AppException
     */
    private byte[] tryRead(@NotNull JsonSensorSingleRequest jsonRequest,
                           @NotNull int intS7DBNumber,
                           int length, int offset) throws AppException {
        long startTime = System.nanoTime();
        try {
            return currentConnector.read(DaveArea.DB, intS7DBNumber, length, offset);
        } catch (Exception ex) {
            String msg = "Ошибка чтения S7";
            String strArgs = "controllerId= " + jsonRequest.getControllerId() + ", intS7DBNumber = " + intS7DBNumber + ", length = " + length + ", offset= " + offset;
            log.warn(AppConst.ERROR_LOG_PREFIX + msg + " args: " + strArgs, ex);
            throw new AppException(TypeException.S7CONTROLLER_ERROR_OF_READ_DATA, msg, strArgs, ex);
        } finally {
            long endTime = System.nanoTime();
            long deltaNano = endTime - startTime;
            totalReadTimeNano += deltaNano;
            totalDataSize += length;
            if (log.isDebugEnabled())
                log.debug("Затрачено время = {} на {} байт данных", TimeUtil.formatTimeAsNano(deltaNano), length);
        }
    }

    /**
     * Все закрыть - завершается приложение
     */
    @Override
    public void close() {
        log.info(prefixLog + " Финишь");
    }

    /**
     * Прочитать блок по нескольким переменным
     *
     * @param jsonRequest
     * @param datum
     * @param rootRequest
     * @return
     * @throws AppException
     * @throws InterruptedException
     */
    private List<JsonSensorResponse> readBlockFromS7(@NotNull JsonRootSensorRequest rootRequest,
                                                     @NotNull JsonSensorSingleRequest jsonRequest,
                                                     @NotNull JsonSensorDatum datum) throws AppException {
        //читаем данные
        int intS7DBNumber = datum.getDataBlock();
        List<JsonSensorResponse> jsonResponseList = new ArrayList<>();
        Set<Integer> lastSuccessDataValIds = new HashSet<>();
        try {

            byte[] dataBytes = null;

            for (JsonSensorDataVal dataVal : datum.getDataVals()) {
                BigDecimal result = BigDecimal.valueOf(Math.random());
                JsonSensorResponse response = jsonHelper.createJsonRequestForData(result, AppConst.JSON_SUCCESS_CODE, dataVal);
                jsonResponseList.add(response);

                lastSuccessDataValIds.add(dataVal.getId());
            }
        } catch (Exception ex) {
            List<JsonSensorResponse> errorJsonResponse = jsonHelper.createJsonRequestWithError(rootRequest, jsonRequest, lastSuccessDataValIds, ex);
            jsonResponseList.addAll(errorJsonResponse);
        }
        return jsonResponseList;
    }
}