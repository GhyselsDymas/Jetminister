package pack.jetminister.data.util;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

public class DateConverter {
    // hoe moet datum geformatteerd zijn


    public static String timeAsString(LocalDateTime localDate) {
        // als er geen datum binnenkomt, niets weergeven
        // anders de datum als string weergeven
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);
        return localDate.format(formatter);
    }

    public static LocalDateTime timeAsLocalDateTime(String stringDate) {
        // als er geen string binnenkomt, niets weergeven
        // anders de string als datum weergeven
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);
        return LocalDateTime.parse(stringDate, formatter);
    }
}
