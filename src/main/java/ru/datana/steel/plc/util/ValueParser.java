package ru.datana.steel.plc.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Персер строк
 */
@Slf4j
public class ValueParser {
    private static final String PREFIX_LOG = "[CONFIG] ";

    public static int parseInt(String strValue, String userNameField) throws AppException {
        log.trace(PREFIX_LOG + ": parse as Int for Field [" + userNameField + "] = " + strValue);
        String args = userNameField + " = '" + strValue + "'";
        if (StringUtils.isEmpty(strValue)) {
            throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "пустое значение", args, null);
        }

        try {
            int value = Integer.parseInt(strValue.trim());
            log.trace(PREFIX_LOG + ": success parsing: [" + userNameField + "] = " + value);
            return value;
        } catch (NumberFormatException ex) {
            throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "не верное целое число", args, ex);
        }

    }

}
