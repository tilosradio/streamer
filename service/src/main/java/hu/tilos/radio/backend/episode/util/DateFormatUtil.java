package hu.tilos.radio.backend.episode.util;

import hu.tilos.radio.backend.util.LocaleUtil;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateFormatUtil {

    public static final SimpleDateFormat YYYY_MM_DD_HHMM = create("yyyy-MM-dd HH:mm");

    public static final SimpleDateFormat YYYY_MM_DD = create("yyyy-MM-dd");

    public static SimpleDateFormat create(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, LocaleUtil.TILOSLOCALE);
        format.setTimeZone(TimeZone.getTimeZone("CET"));
        return format;
    }
}
