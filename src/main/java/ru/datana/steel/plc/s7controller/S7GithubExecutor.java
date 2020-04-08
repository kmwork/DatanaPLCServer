package ru.datana.steel.plc.s7controller;

import com.github.s7connector.api.DaveArea;
import com.github.s7connector.impl.S7TCPConnection;
import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.meta.Controller;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.model.json.request.JsonSensorDataVal;
import ru.datana.steel.plc.model.json.request.JsonSensorDatum;
import ru.datana.steel.plc.model.json.request.JsonSensorSingleRequest;
import ru.datana.steel.plc.model.json.response.JsonRootSensorResponse;
import ru.datana.steel.plc.model.json.response.JsonSensorResponse;
import ru.datana.steel.plc.moka7.EnumSiemensDataType;
import ru.datana.steel.plc.util.*;

import javax.validation.constraints.NotNull;
import java.io.Closeable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Движок от GitHub (упрещенное апи - поключил через зависимость мавен)
 * <p>
 * взято из s7connector
 * https://github.com/s7connector/s7connector
 */
@Slf4j
public class S7GithubExecutor implements Closeable {

    private static final String PREFIX_LOG = "[S7 Контроллер] ";

    /**
     * Коннекты
     * ключ - наш ID в базе данных, значение - коннект Socket с S7
     */
    private Map<Integer, S7TCPConnection> connectionByControllerId = new HashMap<>();

    /**
     * Описание котроллеров по ID
     * ключ - наш ID в базе данных, значение - описание контроллера
     */
    private Map<Integer, Controller> metaByControllerId = new HashMap<>();

    /**
     * Текущий коннект S7
     */
    private S7TCPConnection currentConnector = null;

    /**
     * Утилита по работе Json
     */
    private DatanaJsonHelper jsonHelper = DatanaJsonHelper.getInstance();


    /**
     * настройка сервиса чтения Siemens котроллеров
     *
     * @param controllerMeta
     */
    public void init(@NotNull JsonMetaRootController controllerMeta) {
        metaByControllerId.clear();
        connectionByControllerId.clear();
        for (Controller c : controllerMeta.getControllers()) {
            metaByControllerId.put(c.getId(), c);
        }
        log.debug(PREFIX_LOG + " Прочитаны настройки для {} контролеров : ", metaByControllerId.size(), metaByControllerId.keySet());
    }

    /**
     * Закрыть сокет с S7
     *
     * @param controllerId
     * @return
     */
    private boolean closeS7Connect(@NotNull Integer controllerId) {

        log.info(PREFIX_LOG + " Завершение сессии для controllerId = {}", controllerId);
        connectionByControllerId.remove(controllerId);
        boolean success = false;
        if (currentConnector != null) {
            currentConnector.close();
            currentConnector = null;
            success = true;
        }
        return success;
    }

    /**
     * Установить связь с контроллером
     *
     * @param controllerId
     * @throws AppException
     */
    private void initS7Connection(@NotNull Integer controllerId) throws AppException {
        Controller c = metaByControllerId.get(controllerId);
        String host = c.getIp();
        int rack = c.getRack();
        int slot = c.getSlot();
        int port = c.getPort() == null ? AppConst.S7CONNECTOR_PORT_DEFAULT : c.getPort();
        int timeout = c.getTimeout();

        try {
            currentConnector = connectionByControllerId.get(controllerId);
            if (currentConnector == null) {
                currentConnector = connectionByControllerId.get(controllerId);
                if (currentConnector == null) {
                    currentConnector = new S7TCPConnection(host, rack, slot, port, timeout);
                    connectionByControllerId.put(controllerId, currentConnector);
                }
            }
        } catch (Exception ex) {
            String strArgs = "controllerId = " + controllerId + ", " +
                    "host: " + host + ", rack: " + rack + ", slot: " + slot + ", port: " + port + ", timeout: " + timeout;
            String msg = "Ошибка подключения к контроллеру Сименса: " + ex.getMessage();
            log.error(msg + ". args:" + strArgs, ex);
            throw new AppException(TypeException.S7CONTROLLER_ERROR_OF_CONNECTION, msg, strArgs, ex);
        }
    }

    /**
     * Выполнить запрос клиента по снянию датчиков с нескольких контроллеров
     *
     * @param rootRequest все запросы на контроллеры для один сеанс сканироавания датчиков
     * @return
     */
    public JsonRootSensorResponse run(@NotNull JsonRootSensorRequest rootRequest) {
        LocalDateTime proxyTime = jsonHelper.getCurrentTime();
        List<JsonSensorResponse> jsonResponseList = new ArrayList<>();
        List<JsonSensorSingleRequest> list = rootRequest.getRequest();
        if (list != null)
            for (JsonSensorSingleRequest req : list) {
                if (metaByControllerId.containsKey(req.getControllerId())) {
                    List<JsonSensorResponse> rList = doWorkRequest(rootRequest, req);
                    jsonResponseList.addAll(rList);
                } else {
                    String strArg = "controllerId = " + req.getControllerId();
                    String msg = "Мета информация о контролере S7 = " + req.getControllerId() + " не найдена, есть информация только по ID = " + metaByControllerId.keySet();
                    Exception ex = new AppException(TypeException.S7CONTROLLER__INVALID_NOT_FOUND, msg, strArg, null);
                    log.warn(AppConst.ERROR_LOG_PREFIX + msg);
                    List<JsonSensorResponse> errorResponseList = jsonHelper.createJsonRequestWithError(rootRequest, req, null, ex);
                    jsonResponseList.addAll(errorResponseList);
                }
            }
        JsonRootSensorResponse jsonResult = new JsonRootSensorResponse();
        jsonResult.setRequestDatetime(rootRequest.getRequestDatetime());
        jsonResult.setRequestDatetimeProxy(proxyTime);
        jsonResult.setResponseDatetime(jsonHelper.getCurrentTime());
        jsonResult.setRequestId(rootRequest.getRequestId());
        jsonResult.setTaskId(rootRequest.getTaskId());
        jsonResult.setResponse(jsonResponseList);
        return jsonResult;
    }

    /**
     * Выполнить запрос на один контроллер
     *
     * @param request
     * @return
     */
    private List<JsonSensorResponse> doWorkRequest(@NotNull JsonRootSensorRequest rootRequest,
                                                   @NotNull JsonSensorSingleRequest request) {
        List<JsonSensorResponse> responseList = new ArrayList<>();
        try {

            initS7Connection(request.getControllerId());
            for (JsonSensorDatum datum : request.getData()) {
                List<JsonSensorResponse> responsesByOneRequest = readBlockFromS7(rootRequest, request, datum);
                responseList.addAll(responsesByOneRequest);
            }
        } catch (AppException ex) {
            log.error(AppConst.ERROR_LOG_PREFIX + "Ошибка при чтении S7 для request = " + request, ex);
            List<JsonSensorResponse> response = jsonHelper.createJsonRequestWithError(rootRequest, request, null, ex);

            return response;
        } finally {
            boolean doDisconnect = !metaByControllerId.get(request.getControllerId()).getPermanentConnection();
            if (doDisconnect)
                closeS7Connect(request.getControllerId());
        }

        return responseList;
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
        byte[] dataBytes = null;
        for (int i = 0; i < AppConst.TRY_S7CONTROLLER_READ_OF_COUNT; i++) {
            try {
                Thread.sleep(AppConst.S7_SLEEP_MS);
                dataBytes = currentConnector.read(DaveArea.DB, intS7DBNumber, length, offset);
                break;
            } catch (Exception ex) {
                int tryCount = i + 1;
                String msg = "Ошибка чтения S7. было " + tryCount + " попыток.";
                String strArgs = "controllerId= " + jsonRequest.getControllerId() + ", intS7DBNumber = " + intS7DBNumber + ", length = " + length + ", offset= " + offset;
                log.warn(AppConst.ERROR_LOG_PREFIX + msg + " args: " + strArgs, ex);
                closeS7Connect(jsonRequest.getControllerId());
                if (tryCount == AppConst.TRY_S7CONTROLLER_READ_OF_COUNT)
                    throw new AppException(TypeException.S7CONTROLLER_ERROR_OF_READ_DATA, msg, strArgs, ex);
                initS7Connection(jsonRequest.getControllerId());
            }
        }
        return dataBytes;
    }

    /**
     * Все закрыть - завершается приложение
     */
    @Override
    public void close() {
        log.info(PREFIX_LOG + " Закрытие коннектов для ID = " + connectionByControllerId.keySet());
        Set<Integer> ids = new HashSet<>();
        for (Integer id : connectionByControllerId.keySet()) {
            try {
                boolean isSuccess = closeS7Connect(id);
                if (isSuccess)
                    ids.add(id);
            } catch (Exception ex) {
                log.warn(AppConst.ERROR_LOG_PREFIX + " Ошибка дисконнекта S7 для id = " + id, " Error: " + ex.getMessage());
            }
        }
        log.info(PREFIX_LOG + " Коннекты  весели открытыми с ID = " + ids);
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
        int minOffset = Integer.MAX_VALUE;
        int maxOffset = Integer.MIN_VALUE;

        // опеределяем крайние переменные одного блока (минимум  и максимум для offset) для чтения блока данных
        for (JsonSensorDataVal dataVal : datum.getDataVals()) {
            int offset = dataVal.getOffset();

            // нужно прочитать кратно байтам, если бит то округляем до 1 байта
            EnumSiemensDataType type = EnumSiemensDataType.parseOf(dataVal.getDataType());
            int sizeBytes = (type.getBitCount() + 7) / 8;

            //опередяем крайнее датчики по их смещению offset
            minOffset = Math.min(minOffset, offset);

            //нужно прибавить размер переменной датчика что бы его захватить при чтении блока
            maxOffset = Math.max(maxOffset, offset + sizeBytes);
        }
        int length = maxOffset - minOffset;
        Set<Integer> lastSuccessDataValIds = new HashSet<>();
        try {
            LocalDateTime s7StartTime = LocalDateTime.now();

            //чтение данных
            byte[] dataBytes = tryRead(jsonRequest, intS7DBNumber, length, minOffset);
            LocalDateTime s7EndTime = LocalDateTime.now();
            long deltaNano = s7EndTime.getNano() - s7StartTime.getNano();
            FormatUtils.formatBytes("Чтение с S7 контроллера", dataBytes, EnumFormatBytesType.CLASSIC);

            LocalDateTime time = LocalDateTime.now();
            for (JsonSensorDataVal dataVal : datum.getDataVals()) {
                int bytesOffset = dataVal.getOffset() - minOffset;
                assert bytesOffset >= 0;
                EnumSiemensDataType type = EnumSiemensDataType.parseOf(dataVal.getDataType());
                int intBitPosition = 0;

                //получения позиции если это бит
                if (type == EnumSiemensDataType.TYPE_BIT) {
                    intBitPosition = dataVal.getBitmask().length() - dataVal.getBitmask().indexOf("1");
                }
                BigDecimal result = BitOperationsUtils.doBitsOperations(dataBytes, bytesOffset, type, intBitPosition);
                JsonSensorResponse response = jsonHelper.createJsonRequestForData(result, AppConst.JSON_SUCCESS_CODE, dataVal);

                //время запросов
                response.setS7StartTime(s7StartTime);
                response.setS7EndTime(s7EndTime);
                response.setDeltaNano(deltaNano);
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