package ru.datana.steel.camel.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Формирование байтов в лог
 * в hex и десятичном формате
 */
@Slf4j
public class FormatUtils {

    /**
     * вывод массив байт в лог
     *
     * @param methodName имя метода для пояснения от куда пришли байты
     * @param buffer     массив байт с контроллера
     * @param typeFormat тип как форматировать в логе - в hex или десятичными числами
     * @throws AppException
     */
    public static void formatBytes(String methodName, byte[] buffer, EnumFormatBytesType typeFormat) throws AppException {
        if (!log.isDebugEnabled())
            return;

        String prefixLog = "[DUMP] [Источник:" + methodName + "] ";

        if (buffer == null || buffer.length == 0) {
            log.warn(prefixLog + "<ПУСТО>");
            return;
        }


        if (typeFormat == EnumFormatBytesType.CLASSIC) {
            log.debug(prefixLog + " десятичные числа по байтам: " + Arrays.toString(buffer));

        } else if (typeFormat == EnumFormatBytesType.DECIMAL_NUMBER) {
            log.debug(prefixLog + " как большое целое число: " + new BigInteger(buffer).toString());

        } else if (typeFormat == EnumFormatBytesType.HEX_NUMBER) {
            log.debug(prefixLog + " как hex-число: " + new BigInteger(buffer).toString(16));

        } else {
            String args = "methodName = " + methodName + "byte[] = " + Arrays.toString(buffer) + ", typeFormat = " + typeFormat;
            throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "не понятен формат вывода", args, null);
        }
    }
}
