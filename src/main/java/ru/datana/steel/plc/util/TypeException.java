package ru.datana.steel.plc.util;

import lombok.Getter;
import lombok.ToString;

/**
 * Вид Exception для внешнего потребителя
 */
@ToString
@Getter
public enum TypeException {
    OK(0, "Успешно"),
    SYSTEM_ERROR(-1, "Системная ошибка"),
    INVALID_USER_INPUT_DATA(-2, "Не корректные введённые данные"),
    INVALID_USER_INPUT_META_FILE(-3, "Ошибка чтения файла с мета информацией"),
    INVALID_FORMAT_JSON(-4, "Ошибка форматирования JSON через парсинг в класс"),
    INVALID_CONVERT_TO_JSON(-5, "Ошибка преобразования java class в JSON"),
    S7CONTROLLER_ERROR_OF_CONNECTION(1000, "S7:Ошибка связи с контроллером"),
    S7CONTROLLER_ERROR_OF_READ_DATA(1001, "S7:Ошибка чтения данных с контроллера"),
    S7CONTROLLER__INVALID_NOT_FOUND(1002, "S7:Не найдет контроллер с таким ID.");

    private final int codeError;
    private final String descError;

    TypeException(int codeError, String descError) {
        this.codeError = codeError;
        this.descError = descError;
    }
}
