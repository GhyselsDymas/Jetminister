package pack.jetminister.data.util;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

//date conversion class uses jake wharton's threeten library to make date classes function across all API levels
public class DateConverter {
    public static String timeAsString(LocalDateTime localDate) {
        //choose format to display date
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);
        //return String representation of date
        return localDate.format(formatter);
    }

    public static LocalDateTime timeAsLocalDateTime(String stringDate) {
        //choose format to display date
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);
        //parse String representation of date back to its original object
        return LocalDateTime.parse(stringDate, formatter);
    }
}
