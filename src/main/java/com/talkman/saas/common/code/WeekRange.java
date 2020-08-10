package com.talkman.saas.common.code;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author doger.wang
 * @date 2020/5/28 11:22
 */
public class WeekRange implements Iterable<LocalDate> {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public WeekRange(LocalDate startDate, LocalDate endDate) {
        //check that range is valid (null, start < end)
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static WeekRange between(LocalDate startTime, LocalDate endTime) {
        return new WeekRange(startTime, endTime);
    }


    @Override
    public Iterator<LocalDate> iterator() {
        return stream().iterator();
    }

    public Stream<LocalDate> stream() {
        //把结束时间设置到周末，between不足7天的不计数
        LocalDate with = endDate.with(DayOfWeek.SUNDAY);
        return Stream.iterate(startDate, d -> d.plusDays(7))
                .limit(ChronoUnit.WEEKS.between(startDate, with) + 1);
    }

    public List<String> toList() {
        //could also be built from the stream() method
        List<String> dates = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusWeeks(1)) {
            dates.add(d.getYear() + "-" + d.get(weekFields.weekOfYear()));
        }
        return dates;
    }
}
