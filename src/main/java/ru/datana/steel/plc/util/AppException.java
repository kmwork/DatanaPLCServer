package ru.datana.steel.plc.util;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Главный exception приложения для перекидывания наружу
 */
@Slf4j
@ToString
@Getter
public class AppException extends Exception {
    /**
     * Строка аргументов
     */
    private final String strArgs;

    /**
     * Описание ошибки для пользователя
     */
    private final String msg;
    /**
     * Тип ошибки
     */
    private final TypeException type;

    /**
     * Перво-причина ошибки
     */
    private final Exception mainEx;

    public AppException(TypeException type, String msg, String strArgs, Exception ex) {
        super("Type: " + type + ",\n msg=" + msg + "\n strArgs = " + strArgs, ex);
        this.type = type;
        this.msg = msg;
        this.strArgs = strArgs;
        this.mainEx = ex;
    }
}
