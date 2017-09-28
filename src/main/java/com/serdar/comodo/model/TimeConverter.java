package com.serdar.comodo.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeConverter
{

    public static long milisInHourResolution(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        instant = instant.truncatedTo(ChronoUnit.HOURS);
        return instant.toEpochMilli();
    }
}
