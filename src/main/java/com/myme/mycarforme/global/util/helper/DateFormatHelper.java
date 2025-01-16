package com.myme.mycarforme.global.util.helper;

public class DateFormatHelper {
    public static String toKoreanDateString(String dateStr) {
        String year = dateStr.substring(2, 4);
        String month = dateStr.substring(5, 7);
        return year + "년 " + month + "월";
    }
}
