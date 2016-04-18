package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Represents a {@link Holiday} for holidays that fall on a specific day of the week
 * in the last full week of the month. For example, "Administrative Professional's Day" is
 * on the Wednesday of the last full week of the month. This is NOT the same as the "last
 * Wednesday of the month"
 * <p>
 * In this context, "last full week" means the last week of the month that starts on the normal first
 * day of the week (Sunday or Monday) and has all the remaining weekdays. For example, if
 * the last Sunday in the month is on the 30th, the last full week would start the previous
 * Sunday.
 */
class LastFullWeekOfMonth extends Holiday {

    private Month month;
    private DayOfWeek dayOfWeek;

    public LastFullWeekOfMonth(JsonObject holidayDefJson) {
        super(holidayDefJson);

        month = Month.valueOf(getRule().get("month").asString().toUpperCase());
        dayOfWeek = DayOfWeek.valueOf(getRule().get("dayOfWeek").asString().toUpperCase());
    }


    @Override
    public LocalDate getDate(int year) {

        // get the last sunday in the month (need to revise to then calculate the day we actually want.

        LocalDate startOfLastWeek = DateUtilities.getLastFullWeekOfMonth(year, month);

        return DateUtilities.getSpecifiedDayInWeek(startOfLastWeek, dayOfWeek);


    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getDisplayName());
        sb.append(" occurs on the  ");
        sb.append(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" of the last full week of ");
        sb.append(this.month.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" every year.");

        return sb.toString();
    }


}
