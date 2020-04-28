package ru.datana.steel.plc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.datana.steel.plc.config.AppConst;

import java.io.File;
import java.io.IOException;

/**
 * Парсер мета информации о контроллерах
 */
@Slf4j
public class JsonParserUtil<TYPE> {

    /**
     * дживок по работе Json
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Путь где лежит файл
     */
    private final String dir = System.getProperty(AppConst.SYS_DIR_PROP);

    /**
     * Время изменения файла, который вы прочитали в кеш
     * нужно это чтобы читать по мере изменения файла при работе сервера
     */
    private long prevLastModified = 0;
    private TYPE prevJson = null;
    private final String fileName;

    public JsonParserUtil(String fileName) {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.fileName = fileName;
    }

    public TYPE loadJson(Class<TYPE> clazz) throws AppException {
        if (StringUtils.isEmpty(dir)) {
            String strArgs = AppConst.SYS_DIR_PROP + " = '" + dir + "'";
            throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "пустое значение", strArgs, null);
        }

        File f = new File(dir, fileName);
        try {

            TYPE result;

            //проверка - нужно ли перечитать файл если он изменился со временем запуска сервера
            if (prevLastModified < f.lastModified() || prevJson == null) {
                log.info("[JSON-Parser:Load-Meta] Чтение файла = " + f.getAbsoluteFile());
                result = mapper.readValue(f, clazz);
                prevLastModified = f.lastModified();
                prevJson = result;
            } else
                result = prevJson;
            log.info("[JSON-Parser:Load-Meta] result = " + result);
            log.info("[From file:MetaInfo] " + mapper.writeValueAsString(result));
            return result;
        } catch (IOException ex) {
            String msg = "Ошибка  в программе при чтении файла";
            log.error(msg, ex);
            String strArgs = "File: " + f.getAbsoluteFile();
            throw new AppException(TypeException.INVALID_USER_INPUT_META_FILE, msg, strArgs, ex);
        }

    }
}
