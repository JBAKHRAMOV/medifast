package com.company.util;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String dateToString(LocalDate date){
        return date.format(DateTimeFormatter.ofPattern("d/MM/yyyy"));
    }

    public static LocalDate stringToDate(String str){
        return LocalDate.parse(str,DateTimeFormatter.ofPattern("d.MM.yyyy"));
    }

    public static String timeToString(LocalTime time){
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static LocalTime stringToTime(String str){
        return LocalTime.parse(str, DateTimeFormatter.ofPattern("HH:mm:ss"));

    }
}
