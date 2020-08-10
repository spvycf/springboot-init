package com.talkman.saas.utils;


import com.talkman.saas.common.code.DateFormat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;

/**
 * @author doger.wang
 * @date 2020/3/29 17:03
 */
public class DateUtils {

    /**
     * 计算参数时间到现在的天数
     *
     * @param time
     * @return
     */
    public static Long calDaysUntilNow(Temporal time) {
        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(time, now);
        return between.toDays();
    }

    /**
     * 计算参数时间到现在的分钟数
     *
     * @param time
     * @return
     */
    public static Long calMinutesUntilNow(Temporal time) {
        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(time, now);
        return between.toMinutes() + 1;
    }
    /*    */

    /**
     * 计算参数时间到现在的天数
     *
     * @param time
     * @return
     *//*
    public static Long calDaysUntilNow(LocalDate date) {
        LocalDate now = LocalDate.now();
        Duration between = Duration.between(date, now);
        return between.toDays();
    }*/
    public static String formatTimeToString(TemporalAccessor time, String format) {
        if (StringUtils.isEmpty(format)) {
            format = DateFormat.YYYY_MM_DD_HH_MM_SS;
        }
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(format);
        return pattern.format(time);

    }

    public static String formatTimeToString(TemporalAccessor time) {
        return formatTimeToString(time, "");
    }


    public static long calTimeLeftToMinutesLater(LocalDateTime time, int minutesLater) {
        LocalDateTime later = time.plusMinutes(minutesLater);
        Duration between = Duration.between(LocalDateTime.now(), later);
        return between.toMillis();
    }

    public static String getABDaysHoursTimesString(LocalDateTime rentTime, LocalDateTime returnTime) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        Duration between = Duration.between(rentTime, returnTime);
        long diff = between.toMillis();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = (diff % nd % nh / nm) + 1;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        if (0 > day) {
            return day + "天" + hour + "小时" + min + "分钟";
        } else if (hour > 0) {
            return hour + "小时" + min + "分钟";
        } else {
            return min + "分钟";
        }

    }
}
