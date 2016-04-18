package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Represents a {@link Holiday} for holidays that fall on the LAST
 * occurrence of a specific day of the week in a month.
 * <p>
 * For example, "Memorial Day" is always on the last Monday in May.
 */
final class LastInMonth extends Holiday {

    private Month month;
    private DayOfWeek dayOfWeek;

    public LastInMonth(JsonObject holidayDefJson) {
        super(holidayDefJson);

        // Set the rule-specific fields for this subclass
        month = Month.valueOf(getRule().get("month").asString().toUpperCase());
        dayOfWeek = DayOfWeek.valueOf(getRule().asObject().get("dayOfWeek").asString().toUpperCase());
    }

    @Override
    public LocalDate getDate(int year) {

        return DateUtilities.getLastSpecifiedDayInMonth(year, month, dayOfWeek);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getDisplayName());
        sb.append(" occurs on the last ");
        sb.append(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" in ");
        sb.append(this.month.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" every year.");

        return sb.toString();
    }
}
