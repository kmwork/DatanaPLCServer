package ru.datana.steel.plc.s7controller;

import com.github.s7connector.api.DaveArea;
import com.github.s7connector.impl.S7TCPConnection;
import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.meta.Controller;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;
import ru.datana.steel.plc.model.json.request.JsonDataVal;
import ru.datana.steel.plc.model.json.request.JsonDatum;
import ru.datana.steel.plc.model.json.request.JsonRequest;
import ru.datana.steel.plc.model.json.request.JsonRootRequest;
import ru.datana.steel.plc.model.json.response.JsonError;
import ru.datana.steel.plc.model.json.response.JsonResponse;
import ru.datana.steel.plc.model.json.response.JsonRootResponse;
import ru.datana.steel.plc.moka7.EnumSiemensDataType;
import ru.datana.steel.plc.util.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Движок от GitHub (упрещенное апи - поключил через зависимость мавен)
 * <p>
 * взято из s7connector
 * https://github.com/s7connector/s7connector
 */
@Slf4j
public class S7GithubExecutor {

    private static final String PREFIX_LOG = "[S7 Контроллер] ";
    private Map<Integer, S7TCPConnection> connectionByControllerId = new HashMap<>();
    private Map<Integer, Controller> metaByControllerId = new HashMap<>();
    private S7TCPConnection currentConnecter = null;
    private static int RESPONSE_COUNT = 0;

    public void init(JsonMetaRootController controllerMeta) {
        metaByControllerId.clear();
        connectionByControllerId.clear();
        for (Controller c : controllerMeta.getControllers()) {
            metaByControllerId.put(c.getId(), c);
        }
    }

    private void closeS7Connect(Integer controllerId) {

        log.info(PREFIX_LOG + " Завершение сессии для controllerId = {}", controllerId);
        connectionByControllerId.remove(controllerId);
        if (currentConnecter != null) {
            currentConnecter.close();
            currentConnecter = null;
        }

    }

    private void initS7Connection(Integer controllerId) {
        Controller c = metaByControllerId.get(controllerId);
        currentConnecter = connectionByControllerId.get(controllerId);
        if (currentConnecter == null) {
            currentConnecter = connectionByControllerId.get(controllerId);
            if (currentConnecter == null) {
                int port = c.getPort() == null ? AppConst.S7CONNECTOR_PORT_DEFAULT : c.getPort();
                currentConnecter = new S7TCPConnection(c.getIp(), c.getRack(), c.getSlot(), port, c.getTimeout());
                connectionByControllerId.put(controllerId, currentConnecter);
            }
        }
    }

    public JsonRootResponse run(JsonRootRequest request) throws AppException {

        List<JsonResponse> jsonResponseList = new ArrayList<>();
        for (JsonRequest req : request.getRequest()) {
            List<JsonResponse> rList = doWorkRequest(req);
            jsonResponseList.addAll(rList);
        }
        JsonRootResponse jsonResult = new JsonRootResponse();
        jsonResult.setRequestDatetime(getCurrentTime());
        jsonResult.setRequestId(request.getRequestId());
        jsonResult.setRequestId(genId());
        jsonResult.setTaskId(request.getTaskId());
        jsonResult.setResponse(jsonResponseList);
        return jsonResult;
    }

    private String genId() {
        RESPONSE_COUNT++;
        return "Response:" + System.nanoTime() + ":Index:" + RESPONSE_COUNT;

    }

    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    private List<JsonResponse> doWorkRequest(JsonRequest request) {
        List<JsonResponse> responseList = new ArrayList<>();
        try {

            initS7Connection(request.getControllerId());
            for (JsonDatum datum : request.getData()) {
                List<JsonResponse> responsesByOneRequest = readBlockFromS7(request.getControllerId(), datum);
                responseList.addAll(responsesByOneRequest);
            }
        } catch (Exception e) {
            JsonError jsonError = createJsonError(request, e);
            JsonResponse response = new JsonResponse();
            List<JsonError> errors = new ArrayList<>();
            errors.add(jsonError);
            response.setErrors(errors);
            log.error(AppConst.ERROR_LOG_PREFIX + "Ошибка при чтении S7 для request = " + request);
        } finally {
            boolean doDisconnect = !metaByControllerId.get(request.getControllerId()).getPermanentConnection();
            if (doDisconnect)
                closeS7Connect(request.getControllerId());
        }
        return responseList;
    }

    private JsonError createJsonError(JsonRequest request, Exception e) {
        JsonError jsonError = new JsonError();
        if (e instanceof AppException) {
            AppException appEx = (AppException) e;
            jsonError.setMsg(appEx.getMsg());
            jsonError.setStrArgs(appEx.getStrArgs());
            jsonError.setTypeCode(appEx.getType().getCodeError());
        } else {
            jsonError.setStrArgs("request.ControllerId = " + request.getControllerId());
            jsonError.setMsg(e.getLocalizedMessage());
            jsonError.setTypeCode(TypeException.SYSTEM_ERROR.getCodeError());
        }
        return jsonError;
    }

    private byte[] tryRead(Integer controllerId, int intS7DBNumber, int length, int offset) throws AppException, InterruptedException {
        byte[] dataBytes = null;
        for (int i = 0; i < AppConst.TRY_S7CONTROLLER_READ_OF_COUNT; i++) {
            try {
                Thread.sleep(AppConst.S7_SLEEP_MS);
                dataBytes = currentConnecter.read(DaveArea.DB, intS7DBNumber, length, offset);
                break;
            } catch (Exception e) {
                log.warn(AppConst.ERROR_LOG_PREFIX + "Ошибка чтения S7: controllerId= {}, intS7DBNumber = {}, length = {}, offset= {}", controllerId, intS7DBNumber, length, offset);
                closeS7Connect(controllerId);
                initS7Connection(controllerId);
            }
        }
        return dataBytes;
    }

    private List<JsonResponse> readBlockFromS7(Integer controllerId, JsonDatum datum) throws AppException, InterruptedException {
        //читаем данные
        int intS7DBNumber = ValueParser.parseInt(datum.getDataBlock().substring(2), "Json:DataBlock");

        //   readBlock()
        int minOffset = Integer.MAX_VALUE;
        int maxOffset = Integer.MIN_VALUE;
        for (JsonDataVal dataVal : datum.getDataVals()) {
            int offset = dataVal.getOffset();
            minOffset = Math.min(minOffset, offset);
            maxOffset = Math.max(maxOffset, offset);
        }
        int length = maxOffset - minOffset;
        byte[] dataBytes = tryRead(controllerId, intS7DBNumber, length, minOffset);
        FormatUtils.formatBytes("Чтение с S7 контроллера", dataBytes, EnumFormatBytesType.CLASSIC);

        LocalDateTime time = LocalDateTime.now();
        List<JsonResponse> jsonResponseList = new ArrayList<>();
        for (JsonDataVal dataVal : datum.getDataVals()) {
            int bytesOffset = dataVal.getOffset() - minOffset;
            assert bytesOffset >= 0;
            EnumSiemensDataType type = EnumSiemensDataType.parseOf(dataVal.getDataType());
            int intBitPosition = 0;
            if (type == EnumSiemensDataType.TYPE_BIT) {
                intBitPosition = dataVal.getBitmask().indexOf("1");
            }
            BigDecimal result = BitOperationsUtils.doBitsOperations(dataBytes, bytesOffset, type, intBitPosition);
            JsonResponse response = new JsonResponse();
            response.setControllerDatetime(getCurrentTime());
            response.setData(result.toString());
            response.setId(genId());
            response.setStatus(AppConst.JSON_SUCCESS_CODE);
        }
        return jsonResponseList;
    }
}