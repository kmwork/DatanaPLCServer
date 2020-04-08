package ru.datana.steel.plc.util;


import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class TimeUtil {


    public static String formatTimeAsNano(long timeNano) {
        String result = "";
        if (timeNano < 0) {
            log.warn("[Время меньше нуля] timeNano =" + timeNano);
            result = "минус ";
        }
        timeNano = Math.abs(timeNano);
        Duration duration = Duration.ofNanos(timeNano);
        long seconds = duration.getSeconds();
        if (seconds == 0) {
            if ((timeNano) > 10000) {
                long ms = timeNano / 1000;
                result += ms + "(ms)";
            } else
                result += timeNano + "(nano)";
        } else {
            String positive = String.format(
                    "%d:%02d:%02d",
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60);
            result += positive + "(часы:минуты:секунды)";
        }

        return result;
    }
}
