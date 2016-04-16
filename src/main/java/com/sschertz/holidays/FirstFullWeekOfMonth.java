package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Represents a {@link Holiday} for holidays that fall on a specific day of the week
 * in the first full week of the month.
 *
 * Currently, I have no holidays with this definition. This is included just for
 * completeness.
 * <p>
 * In this context, "first full week" means the first week of the month that starts on the normal first
 * day of the week (Sunday or Monday) and has all the remaining weekdays. For example, if
 * the last Sunday in the month is on the 30th, the last full week would start the previous
 * Sunday.
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

        LocalDate startOfFirstWeek = DateUtilities.getFirstFullWeekOfMonth(year, month);
        return DateUtilities.getSpecifiedDayInWeek(startOfFirstWeek, dayOfWeek);

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
