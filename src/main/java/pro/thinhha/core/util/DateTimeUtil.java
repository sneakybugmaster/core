package pro.thinhha.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility class for date and time operations.
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_DATE;
    public static final DateTimeFormatter CUSTOM_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter CUSTOM_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Convert LocalDateTime to formatted string.
     */
    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(formatter);
    }

    /**
     * Convert LocalDate to formatted string.
     */
    public static String format(LocalDate date, DateTimeFormatter formatter) {
        if (date == null) {
            return null;
        }
        return date.format(formatter);
    }

    /**
     * Parse string to LocalDateTime.
     */
    public static LocalDateTime parseDateTime(String dateTimeString, DateTimeFormatter formatter) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    /**
     * Parse string to LocalDate.
     */
    public static LocalDate parseDate(String dateString, DateTimeFormatter formatter) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * Convert Date to LocalDateTime.
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Convert LocalDateTime to Date.
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Get current date and time.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Get current date.
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
}
