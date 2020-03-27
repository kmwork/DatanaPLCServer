package ru.datana.steel.plc.util;

import lombok.Getter;
import lombok.ToString;

/**
 * Вид Exception для внешеного потребителея
 */
@ToString
@Getter
public enum TypeException {

    OK(0, "Успешно"),
    SYSTEM_ERROR(-1, "Системная ошибка"),
    INVALID_USER_INPUT_DATA(-2, "Не корректные введенные данные"),
    S7CONTROLLER_ERROR_OF_CONNECTION(1000, "S7:Ошибка связи с контроллером"),
    S7CONTROLLER_ERROR_OF_READ_DATA(1001, "S7:Ошибка чтения данных сконтроллера");


    private final int codeError;
    private final String descError;

    TypeException(int codeError, String descError) {
        this.codeError = codeError;
        this.descError = descError;
    }
}
