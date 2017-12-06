package com.chatfuel.test.khivin.events;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class EventDateFormatter {
    private final static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSSXXX")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

    public static String format(Instant moment) {
        return formatter.format(moment);
    }
}
