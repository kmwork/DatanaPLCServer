package ru.datana.steel.plc.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.datana.steel.plc.util.AppException;
import ru.datana.steel.plc.util.TypeException;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class RestSpringConfig implements WebMvcConfigurer {

    @Getter
    @Value("${datana.global.beauty-json}")
    private Boolean beautyJson;

    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        objectMapper = createObjectMapper();
    }

    /**
     * Движок по работе JSON
     *
     * @return
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper.registerModule(new JavaTimeModule());

        //для форматирование LocalDateTime полей
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        if (beautyJson) {
            //режим красирого форматирования
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        return mapper;
    }


    /**
     * Красивое форматирование json
     *
     * @param logPrefix
     * @param fromJson
     * @return
     * @throws AppException
     */
    public String formatBeautyJson(String logPrefix, String fromJson) throws AppException {
        if (!beautyJson) {
            log.info(logPrefix + "не отформатированный json = " + fromJson);
            return fromJson;
        }
        try {
            Object jsonObject = objectMapper.readValue(fromJson, Object.class);
            String prettyJson = objectMapper.writeValueAsString(jsonObject);
            log.info(logPrefix + "formatted-json = " + prettyJson);
            return prettyJson;
        } catch (JsonProcessingException ex) {
            String strArgs = "logPrefix = " + logPrefix + ", fromJson = " + fromJson;
            String msg = "Ошибка форматирования Json";
            throw new AppException(TypeException.INVALID_FORMAT_JSON, msg, strArgs, ex);
        }
    }

    public String toJsonFromObject(String logPrefix, Object jsonObject) throws AppException {
        try {
            log.debug(logPrefix + "convert to json  for " + jsonObject);
            String json = objectMapper.writeValueAsString(jsonObject);
            log.info(logPrefix + "json = " + json);
            return json;
        } catch (JsonProcessingException ex) {
            String strArgs = "logPrefix = " + logPrefix + ", jsonObject = " + jsonObject.getClass() + " toString = " + jsonObject;
            String msg = "Ошибка перобразования в Json";
            throw new AppException(TypeException.INVALID_CONVERT_TO_JSON, msg, strArgs, ex);
        }
    }

    public <T> T parseValue(String fromJson, Class<T> valueType) throws AppException {
        try {
            return objectMapper.readValue(fromJson, valueType);
        } catch (JsonProcessingException ex) {
            String strArg = "fromJson = '" + fromJson + "' as valueType = " + valueType.getName();
            String msg = "Ошибка разбор строки json в объект java";
            log.error(AppConst.ERROR_LOG_PREFIX + msg + ": " + strArg, ex);
            throw new AppException(TypeException.INVALID_FORMAT_JSON, msg, strArg, ex);
        }
    }

    public String toJson(String prefixLog, Object rootJson) throws AppException {
        try {
            String jsonAsString = objectMapper.writeValueAsString(rootJson);
            log.info(prefixLog + "format jsonAsString = " + jsonAsString);
            return jsonAsString;
        } catch (JsonProcessingException ex) {
            String strArg = "prefixLog" + prefixLog + ", rootJson = '" + rootJson;
            String msg = "Ошибка формирование строки из java объекта";
            log.error(AppConst.ERROR_LOG_PREFIX + msg + ": " + strArg, ex);
            throw new AppException(TypeException.INVALID_CONVERT_TO_JSON, msg, strArg, ex);
        }

    }
}