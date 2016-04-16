package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Represents an {@link Holiday} for holidays that fall on a specific day of the week
 * in a specific week of the month. For example, "President's Day" is on the third Monday
 * of February.
 *
 */
class WeekInMonth extends Holiday {
    private Month month;
    private DayOfWeek dayOfWeek;
    private int week;
    private DayOfWeek afterFirst;
    private static final String[] WEEKS_IN_MONTH = {"first", "second", "third", "fourth", "fifth"};


    public WeekInMonth(JsonObject holidayDefJson) {
        super(holidayDefJson);

        // Set the rule-specific fields for this subclass. Should be able to get
        // the rule json from the superclass.

        week = getRule().get("week").asInt();
        month = Month.valueOf(getRule().get("month").asString().toUpperCase());
        dayOfWeek = DayOfWeek.valueOf(getRule().get("dayOfWeek").asString().toUpperCase());

        if (getRule().get("afterFirst") != null)
            afterFirst = DayOfWeek.valueOf(getRule().get("afterFirst").asString().toUpperCase());
    }

    @Override
    public LocalDate getDate(int year) {

        LocalDate date = DateUtilities.getSpecifiedDayInWeekOfMonth(year, month, week, dayOfWeek);

        if (afterFirst == null){
            return date;
        }

        // only want to return date if it is AFTER the first day specified by afterFirst.

        LocalDate afterFirstDate = DateUtilities.getSpecifiedDayInWeekOfMonth(year, month, 1, afterFirst);
        if (date.isAfter(afterFirstDate)) {
            return date;
        } else {
            return date.plusDays(NUM_DAYS_IN_WEEK);
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getDisplayName());
        sb.append(" occurs on the ");
        sb.append(WEEKS_IN_MONTH[week - 1] + " ");
        sb.append(this.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" of ");
        sb.append(this.month.getDisplayName(TextStyle.FULL, Locale.US));

        if (afterFirst != null) {
            sb.append(" (following the first ");
            sb.append(afterFirst.getDisplayName(TextStyle.FULL,Locale.US));
            sb.append(" of " + this.month.getDisplayName(TextStyle.FULL,Locale.US) + ")");
        }
        sb.append(" every year.");

        return sb.toString();
    }
}
