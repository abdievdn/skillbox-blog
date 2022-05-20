package main.service.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimestampUtil {

    public static long encode(LocalDateTime time) {
        ZonedDateTime zdt = ZonedDateTime.of(time, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli() / 1000;
    }

    public static LocalDateTime decode(Long timestamp) {
        return Instant.ofEpochMilli(timestamp * 1000).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
