package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Represents a {@link Holiday} for holidays that fall on a specific date every year.
 * For example, Christmas Day is always on December 25th.
 *
 * A {@code StaticDate} holiday CAN be defined with a special {@code forceWeekday}
 * flag. This is typically used to calcuate when static holiday is actully observed
 * if it falls on a weekend. For instance, if Christmas Day falls on a Sunday, it
 * is normally observed (for the purposes of days off) on Monday. Note that
 * this functionality is not yet implemented.
 */
final class StaticDate extends Holiday {

    private Month month;
    private int day;
    private boolean forceWeekday = false;

    StaticDate(JsonObject holidayDefJson) {
        super(holidayDefJson);

        // Set the rule-specific fields for this subclass
        month = Month.valueOf(getRule().get("month").asString().toUpperCase());
        day = getRule().get("day").asInt();

        JsonValue weekday = holidayDefJson.get("rule").asObject().get("forceWeekday");
        forceWeekday = (weekday != null) && weekday.asBoolean();
    }

    @Override
    public LocalDate getDate(int year) {
        final LocalDate calendarDate = LocalDate.of(year, month, day);
        if (forceWeekday){
            return getObservedDateFrom(calendarDate);
        } else {
            return calendarDate;
        }
    }

    private LocalDate getObservedDateFrom(LocalDate calendarDate) {
        final DayOfWeek dayOfWeek = calendarDate.getDayOfWeek();

        if (dayOfWeek.equals(DayOfWeek.SATURDAY)) {
            return LocalDate.of(calendarDate.getYear(), month, day - 1);

        } else if (dayOfWeek.equals(DayOfWeek.SUNDAY)) {
            return LocalDate.of(calendarDate.getYear(), month, day + 1);

        } else {
            return calendarDate;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getDisplayName());
        sb.append(" occurs on ");
        sb.append(this.month.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" ");
        sb.append(this.day);
        sb.append(" every year. ");
        if (forceWeekday == true) {
            sb.append("If it falls on a weekend, the holidays is observed on either the Friday before or Monday after");
        }

        return sb.toString();
    }

}
