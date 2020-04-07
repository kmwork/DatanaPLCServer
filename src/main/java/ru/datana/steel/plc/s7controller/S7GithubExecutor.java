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
    private Map<Integer, S7TCPConnection> connectionByControllerId = new HashMap<>();
    private Map<Integer, Controller> metaByControllerId = new HashMap<>();
    private S7TCPConnection currentConnector = null;
    private DatanaJsonHelper jsonHelper = DatanaJsonHelper.getInstance();


    public void init(JsonMetaRootController controllerMeta) {
        metaByControllerId.clear();
        connectionByControllerId.clear();
        for (Controller c : controllerMeta.getControllers()) {
            metaByControllerId.put(c.getId(), c);
        }
        log.debug(PREFIX_LOG + " Прочитаны настройки для {} контролеров : ", metaByControllerId.size(), metaByControllerId.keySet());
    }

    private boolean closeS7Connect(Integer controllerId) {

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

    private void initS7Connection(Integer controllerId) throws AppException {
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

    public JsonRootSensorResponse run(JsonRootSensorRequest request) {
        LocalDateTime proxyTime = jsonHelper.getCurrentTime();

        List<JsonSensorResponse> jsonResponseList = new ArrayList<>();
        List<JsonSensorSingleRequest> list = request.getRequest();
        if (list != null)
            for (JsonSensorSingleRequest req : list) {
                if (metaByControllerId.containsKey(req.getControllerId())) {
                    List<JsonSensorResponse> rList = doWorkRequest(req);
                    jsonResponseList.addAll(rList);
                } else {
                    log.warn(AppConst.ERROR_LOG_PREFIX +
                                    "Мета информация о контролере S7 = {} не найдена, есть информация только по ID = {}",
                            req.getControllerId(), metaByControllerId.keySet());
                }
            }
        JsonRootSensorResponse jsonResult = new JsonRootSensorResponse();
        jsonResult.setRequestDatetime(request.getRequestDatetime());
        jsonResult.setRequestDatetimeProxy(proxyTime);
        jsonResult.setResponseDatetime(jsonHelper.getCurrentTime());
        jsonResult.setRequestId(request.getRequestId());
        jsonResult.setTaskId(request.getTaskId());
        jsonResult.setResponse(jsonResponseList);
        return jsonResult;
    }

    private List<JsonSensorResponse> doWorkRequest(JsonSensorSingleRequest request) {
        List<JsonSensorResponse> responseList = new ArrayList<>();
        try {

            initS7Connection(request.getControllerId());
            for (JsonSensorDatum datum : request.getData()) {
                List<JsonSensorResponse> responsesByOneRequest = readBlockFromS7(request, datum);
                responseList.addAll(responsesByOneRequest);
            }
        } catch (Exception ex) {
            JsonSensorResponse response = jsonHelper.createJsonRequestWithError(request, ex);
            responseList.add(response);
            log.error(AppConst.ERROR_LOG_PREFIX + "Ошибка при чтении S7 для request = " + request, ex);
        } finally {
            boolean doDisconnect = !metaByControllerId.get(request.getControllerId()).getPermanentConnection();
            if (doDisconnect)
                closeS7Connect(request.getControllerId());
        }
        return responseList;
    }


    private byte[] tryRead(JsonSensorSingleRequest jsonRequest, int intS7DBNumber, int length, int offset) throws AppException {
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

    private List<JsonSensorResponse> readBlockFromS7(JsonSensorSingleRequest jsonRequest, JsonSensorDatum datum) throws AppException, InterruptedException {
        //читаем данные
        int intS7DBNumber = ValueParser.parseInt(datum.getDataBlock().substring(2), "Json:DataBlock");
        List<JsonSensorResponse> jsonResponseList = new ArrayList<>();
        int minOffset = Integer.MAX_VALUE;
        int maxOffset = Integer.MIN_VALUE;
        for (JsonSensorDataVal dataVal : datum.getDataVals()) {
            int offset = dataVal.getOffset();
            EnumSiemensDataType type = EnumSiemensDataType.parseOf(dataVal.getDataType());
            int sizeBytes = (type.getBitCount() + 7) / 8;
            minOffset = Math.min(minOffset, offset);
            maxOffset = Math.max(maxOffset, offset + sizeBytes);
        }
        int length = maxOffset - minOffset;
        try {
            byte[] dataBytes = tryRead(jsonRequest, intS7DBNumber, length, minOffset);
            FormatUtils.formatBytes("Чтение с S7 контроллера", dataBytes, EnumFormatBytesType.CLASSIC);

            LocalDateTime time = LocalDateTime.now();
            for (JsonSensorDataVal dataVal : datum.getDataVals()) {
                int bytesOffset = dataVal.getOffset() - minOffset;
                assert bytesOffset >= 0;
                EnumSiemensDataType type = EnumSiemensDataType.parseOf(dataVal.getDataType());
                int intBitPosition = 0;
                if (type == EnumSiemensDataType.TYPE_BIT) {
                    intBitPosition = dataVal.getBitmask().length() - dataVal.getBitmask().indexOf("1");
                }
                BigDecimal result = BitOperationsUtils.doBitsOperations(dataBytes, bytesOffset, type, intBitPosition);
                JsonSensorResponse response = jsonHelper.createJsonRequest(result, AppConst.JSON_SUCCESS_CODE);
                jsonResponseList.add(response);
            }
        } catch (Exception ex) {
            JsonSensorResponse jsonResponse = jsonHelper.createJsonRequestWithError(jsonRequest, ex);
            jsonResponseList.add(jsonResponse);
        }
        return jsonResponseList;
    }
}