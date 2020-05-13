package ru.datana.steel.camel.moka7;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import ru.datana.steel.camel.util.AppException;
import ru.datana.steel.camel.util.TypeException;

@ToString
@AllArgsConstructor
public enum EnumSiemensDataType {
    TYPE_BYTE(8, false),
    TYPE_BIT(1, false),
    TYPE_UNSIGNED_WORD(16, false),
    TYPE_UNSIGNED_DOUBLE_WORD(32, false),
    TYPE_REAL(32, true);

    @Getter
    private final int bitCount;

    @Getter
    private final boolean isSigned;

    public static EnumSiemensDataType parseOf(String strType) throws AppException {
        if (StringUtils.isEmpty(strType))
            throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "Не указан тип данны для S7", "EnumSiemensDataType is empty", null);
        strType = strType.toLowerCase().trim();
        switch (strType) {
            case "bool":
                return TYPE_BIT;
            case "byte":
                return TYPE_BYTE;
            case "word":
                return TYPE_UNSIGNED_WORD;
            case "dword":
                return TYPE_UNSIGNED_DOUBLE_WORD;
            case "real":
                return TYPE_REAL;
        }
        throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "Не понятный тип данных S7", "EnumSiemensDataType is '" + strType + "'", null);
    }
}
