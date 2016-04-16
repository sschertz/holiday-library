package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.LocalDate;

/**
 * Created by schertzs on 2/22/2016.
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
        return "DaysBeforeHoliday{}";
    }
}
