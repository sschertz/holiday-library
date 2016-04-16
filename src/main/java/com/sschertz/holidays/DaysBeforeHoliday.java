package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.LocalDate;

/**
 * Not yet impemented. This will represent a holiday that is defined as a specified
 * number of days before another holiday.
 * <p>
 * For now, just returns today.
 */
public class DaysBeforeHoliday extends Holiday {

    public DaysBeforeHoliday(JsonObject holidayDefJson) {
        super(holidayDefJson);
    }

    @Override
    public LocalDate getDate(int year) {
        return LocalDate.now();
    }

    @Override
    public String toString() {
        return "DaysBeforeHoliday is not yet implemented";
    }
}
