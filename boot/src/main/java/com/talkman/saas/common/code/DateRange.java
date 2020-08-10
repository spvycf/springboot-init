package com.talkman.saas.common.code;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author doger.wang
 * @date 2020/5/28 11:22
 */
public class DateRange implements Iterable<LocalDate> {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        //check that range is valid (null, start < end)
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static DateRange between(LocalDate startTime, LocalDate endTime) {
        return new DateRange(startTime, endTime);
    }


    @Override
    public Iterator<LocalDate> iterator() {
        return stream().iterator();
    }

    public Stream<LocalDate> stream() {
        return Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }

    public List<LocalDate> toList() { //could also be built from the stream() method
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            dates.add(d);
        }
        return dates;
    }
}
