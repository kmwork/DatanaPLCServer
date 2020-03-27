package ru.datana.steel.plc.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Формирование байтов в лог
 * в hex и десятичном формате
 */
@Slf4j
public class FormatUtils {


    public static void formatBytes(String methodName, byte[] buffer, EnumFormatBytesType typeFormat) throws AppException {
        String prefixLog = "[DUMP] [Источник:" + methodName + "] ";

        if (buffer == null || buffer.length == 0) {
            log.warn(prefixLog + "<ПУСТО>");
            return;
        }


        if (typeFormat == EnumFormatBytesType.CLASSIC) {
            log.info(prefixLog + " десятичные числа по байтам: " + Arrays.toString(buffer));

        } else if (typeFormat == EnumFormatBytesType.DECIMAL_NUMBER) {
            log.info(prefixLog + " как большое целое число: " + new BigInteger(buffer).toString());

        } else if (typeFormat == EnumFormatBytesType.HEX_NUMBER) {
            log.info(prefixLog + " как hex-число: " + new BigInteger(buffer).toString(16));

        } else {
            String args = "methodName = " + methodName + "byte[] = " + Arrays.toString(buffer) + ", typeFormat = " + typeFormat;
            throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "не понятен формат вывода", args, null);
        }
    }
}