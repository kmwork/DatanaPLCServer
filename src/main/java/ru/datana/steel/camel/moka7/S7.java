package ru.datana.steel.camel.moka7;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("ALL")
public class S7 {
    // Returns the bit at Pos.Bit
    public static boolean GetBitAt(byte[] Buffer, int Pos, int Bit) {
        int Value = Buffer[Pos] & 0x0FF;
        byte[] Mask = {
                (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08,
                (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80
        };
        if (Bit < 0) Bit = 0;
        if (Bit > 7) Bit = 7;

        return (Value & Mask[Bit]) != 0;
    }

    /**
     * Returns a 16 bit unsigned value : from 0 to 65535 (2^16-1)
     *
     * @param Buffer
     * @param Pos    start position
     * @return
     */
    public static int GetWordAt(byte[] Buffer, int Pos) {
        int hi = (Buffer[Pos] & 0x00FF);
        int lo = (Buffer[Pos + 1] & 0x00FF);
        return (hi << 8) + lo;
    }

    // Returns a 16 bit signed value : from -32768 to 32767
    public static int GetShortAt(byte[] Buffer, int Pos) {
        int hi = (Buffer[Pos]);
        int lo = (Buffer[Pos + 1] & 0x00FF);
        return ((hi << 8) + lo);
    }

    // Returns a 32 bit unsigned value : from 0 to 4294967295 (2^32-1)
    public static long GetDWordAt(byte[] Buffer, int Pos) {
        long result;
        result = Buffer[Pos] & 0x0FF;
        result <<= 8;
        result += Buffer[Pos + 1] & 0x0FF;
        result <<= 8;
        result += Buffer[Pos + 2] & 0x0FF;
        result <<= 8;
        result += Buffer[Pos + 3] & 0x0FF;
        return result;
    }

    // Returns a 32 bit signed value : from 0 to 4294967295 (2^32-1)
    public static int GetDIntAt(byte[] Buffer, int Pos) {
        int result;
        result = Buffer[Pos];
        result <<= 8;
        result += (Buffer[Pos + 1] & 0x0FF);
        result <<= 8;
        result += (Buffer[Pos + 2] & 0x0FF);
        result <<= 8;
        result += (Buffer[Pos + 3] & 0x0FF);
        return result;
    }

    // Returns a 32 bit floating point
    public static float GetFloatAt(byte[] Buffer, int Pos) {
        int IntFloat = GetDIntAt(Buffer, Pos);
        return Float.intBitsToFloat(IntFloat);
    }

    // Returns an ASCII string
    public static String GetStringAt(byte[] Buffer, int Pos, int MaxLen) {
        byte[] StrBuffer = new byte[MaxLen];
        System.arraycopy(Buffer, Pos, StrBuffer, 0, MaxLen);
        String S;
        S = new String(StrBuffer, StandardCharsets.UTF_8); // the charset is UTF-8
        return S;
    }

    public static String GetPrintableStringAt(byte[] Buffer, int Pos, int MaxLen) {
        byte[] StrBuffer = new byte[MaxLen];
        System.arraycopy(Buffer, Pos, StrBuffer, 0, MaxLen);
        for (int c = 0; c < MaxLen; c++) {
            if ((StrBuffer[c] < 31) || (StrBuffer[c] > 126))
                StrBuffer[c] = 46; // '.'
        }
        String S;
        S = new String(StrBuffer, StandardCharsets.UTF_8); // the charset is UTF-8
        return S;
    }

    public static Date GetDateAt(byte[] Buffer, int Pos) {
        int Year, Month, Day, Hour, Min, Sec;
        Calendar S7Date = Calendar.getInstance();

        Year = S7.BCDtoByte(Buffer[Pos]);
        if (Year < 90)
            Year += 2000;
        else
            Year += 1900;

        Month = S7.BCDtoByte(Buffer[Pos + 1]) - 1;
        Day = S7.BCDtoByte(Buffer[Pos + 2]);
        Hour = S7.BCDtoByte(Buffer[Pos + 3]);
        Min = S7.BCDtoByte(Buffer[Pos + 4]);
        Sec = S7.BCDtoByte(Buffer[Pos + 5]);

        S7Date.set(Year, Month, Day, Hour, Min, Sec);

        return S7Date.getTime();
    }

    public static void SetBitAt(byte[] Buffer, int Pos, int Bit, boolean Value) {
        byte[] Mask = {
                (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08,
                (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80
        };
        if (Bit < 0) Bit = 0;
        if (Bit > 7) Bit = 7;

        if (Value)
            Buffer[Pos] = (byte) (Buffer[Pos] | Mask[Bit]);
        else
            Buffer[Pos] = (byte) (Buffer[Pos] & ~Mask[Bit]);
    }

    public static void SetWordAt(byte[] Buffer, int Pos, int Value) {
        int Word = Value & 0x0FFFF;
        Buffer[Pos] = (byte) (Word >> 8);
        Buffer[Pos + 1] = (byte) (Word & 0x00FF);
    }

    public static void SetShortAt(byte[] Buffer, int Pos, int Value) {
        Buffer[Pos] = (byte) (Value >> 8);
        Buffer[Pos + 1] = (byte) (Value & 0x00FF);
    }

    public static void SetDWordAt(byte[] Buffer, int Pos, long Value) {
        long DWord = Value & 0x0FFFFFFFF;
        Buffer[Pos + 3] = (byte) (DWord & 0xFF);
        Buffer[Pos + 2] = (byte) ((DWord >> 8) & 0xFF);
        Buffer[Pos + 1] = (byte) ((DWord >> 16) & 0xFF);
        Buffer[Pos] = (byte) ((DWord >> 24) & 0xFF);
    }

    public static void SetDIntAt(byte[] Buffer, int Pos, int Value) {
        Buffer[Pos + 3] = (byte) (Value & 0xFF);
        Buffer[Pos + 2] = (byte) ((Value >> 8) & 0xFF);
        Buffer[Pos + 1] = (byte) ((Value >> 16) & 0xFF);
        Buffer[Pos] = (byte) ((Value >> 24) & 0xFF);
    }

    public static void SetFloatAt(byte[] Buffer, int Pos, float Value) {
        int DInt = Float.floatToIntBits(Value);
        SetDIntAt(Buffer, Pos, DInt);
    }

    public static void SetDateAt(byte[] Buffer, int Pos, Date DateTime) {
        int Year, Month, Day, Hour, Min, Sec, Dow;
        Calendar S7Date = Calendar.getInstance();
        S7Date.setTime(DateTime);

        Year = S7Date.get(Calendar.YEAR);
        Month = S7Date.get(Calendar.MONTH) + 1;
        Day = S7Date.get(Calendar.DAY_OF_MONTH);
        Hour = S7Date.get(Calendar.HOUR_OF_DAY);
        Min = S7Date.get(Calendar.MINUTE);
        Sec = S7Date.get(Calendar.SECOND);
        Dow = S7Date.get(Calendar.DAY_OF_WEEK);

        if (Year > 1999)
            Year -= 2000;

        Buffer[Pos] = ByteToBCD(Year);
        Buffer[Pos + 1] = ByteToBCD(Month);
        Buffer[Pos + 2] = ByteToBCD(Day);
        Buffer[Pos + 3] = ByteToBCD(Hour);
        Buffer[Pos + 4] = ByteToBCD(Min);
        Buffer[Pos + 5] = ByteToBCD(Sec);
        Buffer[Pos + 6] = 0;
        Buffer[Pos + 7] = ByteToBCD(Dow);
    }

    public static int BCDtoByte(byte B) {
        return ((B >> 4) * 10) + (B & 0x0F);
    }

    public static byte ByteToBCD(int Value) {
        return (byte) (((Value / 10) << 4) | (Value % 10));
    }

}
