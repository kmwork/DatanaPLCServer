package ru.datana.steel.plc.util;


import java.time.Duration;

public class TimeUtil {

    public static String formatTimeAsNano(long timeNano) {
        Duration duration = Duration.ofNanos(timeNano);
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        String result = seconds < 0 ? "-" + positive : positive;
        return "Время = " + result + ", как ms = " + (timeNano / 1000);
    }
}
