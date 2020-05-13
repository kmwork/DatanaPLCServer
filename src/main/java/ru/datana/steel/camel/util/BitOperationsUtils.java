package ru.datana.steel.camel.util;

import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.camel.moka7.EnumSiemensDataType;
import ru.datana.steel.camel.moka7.S7;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Битовые преобразование сдвигов и бинарных AND
 */
@Slf4j
public class BitOperationsUtils {

    private static final String PREFIX_LOG = "[Приведение к типу] ";

    /**
     * Унификация типов данных к одному типу BigDecimal
     *
     * @param data           массив байт представляющие число (мы читаем блоками для экономии а потом вырезаем из массива)
     * @param bytesOffset    смещение где число размещено
     * @param type           тип числа
     * @param intBitPosition если это бит то его позиция в байте
     * @return число унифицированное под Numeric по PostgreSQL
     * @throws AppException
     */
    public static BigDecimal doBitsOperations(byte[] data, int bytesOffset, EnumSiemensDataType type, int intBitPosition) throws AppException {

        //проверка на пустые данные
        if (data == null || data.length <= bytesOffset) {
            log.info(PREFIX_LOG + " Пустые данные");
            return null;
        }


        // преобразования согласно типу данных
        BigDecimal result;
        if (type == EnumSiemensDataType.TYPE_BIT) {
            boolean bitBoolean = S7.GetBitAt(data, bytesOffset, intBitPosition);
            result = new BigDecimal(bitBoolean ? 1 : 0);
        } else if (type == EnumSiemensDataType.TYPE_BYTE) {
            result = new BigDecimal(data[bytesOffset]);
        } else if (type == EnumSiemensDataType.TYPE_UNSIGNED_WORD) {
            int valueWord = S7.GetWordAt(data, bytesOffset);
            result = new BigDecimal(valueWord);
        } else if (type == EnumSiemensDataType.TYPE_UNSIGNED_DOUBLE_WORD) {
            long valueLong = S7.GetDWordAt(data, bytesOffset);
            result = new BigDecimal(valueLong);
        } else if (type == EnumSiemensDataType.TYPE_REAL) {
            float valueFloat = S7.GetFloatAt(data, bytesOffset);
            result = new BigDecimal(valueFloat);
        } else {
            String args = "тип = " + type + " значение в байт = " + Arrays.toString(data);
            throw new AppException(TypeException.INVALID_USER_INPUT_DATA, " Не определен тип данных", args, null);
        }

        if (log.isDebugEnabled())
            log.debug(PREFIX_LOG + "[Тип: " + type + "] = " + result);
        return result;
    }
}
