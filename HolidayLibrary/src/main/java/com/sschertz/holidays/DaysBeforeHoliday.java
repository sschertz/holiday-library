package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.LocalDate;

/**
 * Represents a {@link Holiday} for holidays that are calculated based on the date
 * of a different holiday. For example, Good Friday is two days before Easter Sunday.
 *
 * The other holiday MUST be defined in the same JSON configuration file.
 *
 * Because some of these holidays have special rules, this type provides a "specialDescription"
 * that can be used to provide a more accurate description of the rules. For instance,
 * the straight numeric rule for Ash Wednesday is 46 days before Easter, but most people
 * would probably not think of it that way. If a specialDescription is provided in the
 * JSON rules, that descrption is used in the {@link DaysBeforeHoliday#toString()} method.
 *
 */
class DaysBeforeHoliday extends Holiday {

    private int daysBefore;
    private Holiday otherHoliday;
    private String specialDescription = null;

    DaysBeforeHoliday(JsonObject holidayDefJson, Holiday otherHoliday) {
        super(holidayDefJson);

        daysBefore = getRule().get("daysBefore").asInt();
        this.otherHoliday = otherHoliday;


        if (getRule().get("specialDescription") != null){
            this.specialDescription = getRule().get("specialDescription").asString();
        }
    }

    @Override
    public LocalDate getDate(int year) {

        // Get the date of the other holiday, and then add the specified days
        LocalDate date = otherHoliday.getDate(year);

        return date.minusDays(daysBefore);
    }

    @Override
    public String toString() {

        if (specialDescription != null){
            return this.getDisplayName() +
                    " " +
                    specialDescription;
        } else {
            return this.getDisplayName() +
                    " occurs " +
                    daysBefore +
                    (daysBefore > 1 ? " days before " : " day before ") +
                    otherHoliday.getDisplayName();
        }
    }
}
