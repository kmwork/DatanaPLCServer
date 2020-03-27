package ru.datana.steel.plc.s7controller;

import com.github.s7connector.impl.S7TCPConnection;
import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.plc.config.AppConts;
import ru.datana.steel.plc.model.json.meta.Controller;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;
import ru.datana.steel.plc.model.json.request.DataVal;
import ru.datana.steel.plc.model.json.request.Datum;
import ru.datana.steel.plc.model.json.request.JsonRootRequest;
import ru.datana.steel.plc.model.json.request.Request;
import ru.datana.steel.plc.model.json.response.JsonRootResponse;
import ru.datana.steel.plc.model.json.response.Response;
import ru.datana.steel.plc.util.AppException;
import ru.datana.steel.plc.util.EnumFormatBytesType;
import ru.datana.steel.plc.util.FormatUtils;
import ru.datana.steel.plc.util.ValueParser;

import java.time.LocalDateTime;
import java.util.HashMap;
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
                int port = c.getPort() == null ? AppConts.S7CONNECTOR_PORT_DEFAULT : c.getPort();
                currentConnecter = new S7TCPConnection(c.getIp(), c.getRack(), c.getSlot(), port, c.getTimeout());
                connectionByControllerId.put(controllerId, currentConnecter);
            }
        }
    }

    public JsonRootResponse run(JsonRootRequest request) throws AppException {

        try {
            for (Request req : request.getRequest()) {
                doWorkRequest(req);
            }
        } catch (Exception e) {
            log.error("[App-Error: Аварийное завершение программы: ", e);
        }
        JsonRootResponse jsonResult = new JsonRootResponse();
        jsonResult.setRequestDatetime(getCurrentTime());
        jsonResult.setRequestId(request.getRequestId());
        jsonResult.setRequestId(genId());
        jsonResult.setTaskId(request.getTaskId());
        return jsonResult;
    }

    private String genId() {
        RESPONSE_COUNT++;
        return "Response:" + System.nanoTime() + ":Index:" + RESPONSE_COUNT;

    }

    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    private Response doWorkRequest(Request r) {
        try {
            initS7Connection(r.getControllerId());
            for (Datum datum : r.getData()) {
                byte[] dump = readBlockFromS7(r.getControllerId(), datum);
                convertDumpToJson(dump, datum);
            }
        } catch (Exception e) {
        } finally {
            boolean doDisconnect = !metaByControllerId.get(r.getControllerId()).getPermanentConnection();
            if (doDisconnect)
                closeS7Connect(r.getControllerId());
        }
    }

    private void convertDumpToJson(byte[] dump, Datum datum) {
        LocalDateTime time = LocalDateTime.now();
        for (DataVal dataVal : datum.getDataVals()) {

        }
    }


    private byte[] tryRead(Integer controllerId, int intS7DBNumber, int length, int offset) throws AppException, InterruptedException {
        try {
            boolean success = false;
            int tryCount = 0;
            for (int i = 0; i < AppConts.TRY_S7CONTROLLER_READ_OF_COUNT; i++)
                try {
                    Thread.sleep(AppConts.S7_SLEEP_MS);
                    byte[] dataBytes = currentConnecter.read(EnumS7Area.S7AreaDB.getS7AreaCode()., intS7DBNumber, length, offset);
                    return dataBytes;
                } catch (Exception e) {
                    log.warn(AppConts.ERROR_LOG_PREFIX + "Ошибка чтения S7: controllerId= {}, intS7DBNumber = {}, length = {}, offset= {}", controllerId, intS7DBNumber, length, offset);
                    closeS7Connect(controllerId);
                    initS7Connection(controllerId);
                }
        }
    }

    private byte[] readBlockFromS7(Integer controllerId, Datum datum) throws AppException, InterruptedException {
        //читаем данные
        int intS7DBNumber = ValueParser.parseInt(datum.getDataBlock().substring(2), "Json:DataBlock");

        //   readBlock()
        int minOffset = Integer.MAX_VALUE;
        int maxOffset = Integer.MIN_VALUE;
        for (DataVal dataVal : datum.getDataVals()) {
            int offset = dataVal.getOffset();
            minOffset = Math.min(minOffset, offset);
            maxOffset = Math.max(maxOffset, offset);
        }
        int length = maxOffset - minOffset;
        byte[] dataBytes = tryRead(controllerId, intS7DBNumber, length, minOffset);
        FormatUtils.formatBytes("Чтение с S7 контроллера", dataBytes, EnumFormatBytesType.CLASSIC);
        return dataBytes;
    }
}