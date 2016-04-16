package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Created by saraschertz on 2/21/16.
 */
class FirstFullWeekOfMonth extends Holiday {
    private Month month;
    private DayOfWeek dayOfWeek;

    public FirstFullWeekOfMonth(JsonObject holidayDefJson) {
        super(holidayDefJson);

        // Set the rule-specific fields for this subclass.

        month = Month.valueOf(getRule().get("month").asString().toUpperCase());
        dayOfWeek = DayOfWeek.valueOf(getRule().get("dayOfWeek").asString().toUpperCase());
    }

    @Override
    public LocalDate getDate(int year) {

        return DateUtilities.getFirstFullWeekOfMonth(year,month);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getDisplayName());
        sb.append(" occurs on the ");
        sb.append(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" of the first full week of ");
        sb.append(this.month.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" every year.");

        return sb.toString();
    }
}
