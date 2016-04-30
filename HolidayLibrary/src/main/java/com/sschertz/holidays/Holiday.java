package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Represents the rules for calculating the date of a particular holiday. Use {@link HolidayFactory} to
 * get an object of this class.
 * <p>
 * Subclasses must implement the methods that do the calculation.
 */
public abstract class Holiday implements Comparable {

    protected static int NUM_DAYS_IN_WEEK = 7;
    private String name, displayName;
    private RuleType type;
    private JsonObject rule;

    /**
     * Package-private constructor for a {@code Holiday} object. Creates a new {@code Holiday}
     * object from the provided JSON.
     * <p>
     * Use {@link HolidayFactory} to get an object of this class.
     *
     * @param holidayDefJson a {@code JsonObject} containing the definition for the holiday.
     */
    Holiday(JsonObject holidayDefJson) {
        // Get all the data out of the JSON rule and put into our fields.
        name = holidayDefJson.get("name").asString();
        displayName = holidayDefJson.get("displayName").asString();
        type = RuleType.valueOf(holidayDefJson.get("type").asString().toUpperCase());
        rule = holidayDefJson.get("rule").asObject();
    }


    /**
     * Compares this {@code Holiday} with the specified {@code Holiday} object.
     *
     * This currently uses display name to sort {@code Holiday} objects alphabetically. Future
     * enhancment to add options for sorting by date.
     *
     * @param o the {@code Holiday} object to be compared.
     * @return a negative integer, zero, or a positive integer depending on whether the display
     * name for this {@code Holiday} is less than, equal to, or greater than the specified
     * {@code Holiday} (alphabetically).
     */
    @Override
    public int compareTo(Object o) {
        // TODO: add options for sorting by either name or date

        String thisString = this.getDisplayName();
        Holiday thatHoliday = (Holiday) o;
        String thatString = thatHoliday.getDisplayName();

        return thisString.compareToIgnoreCase(thatString);
    }

    /**
     * Gets the name for the holiday
     *
     * @return the name for the holiday
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the display name for the holiday
     *
     * @return the display name for the holiday
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the {@link RuleType} for this holiday. The {@code RuleType}
     * represents the rules used to calculate the date for this holiday.
     *
     * @return a {@link RuleType} object representing the rules for calculating
     * the date for this holiday.
     */
    public RuleType getType() {
        return type;
    }

    /**
     * Returns the {@code JsonObject} that defines the rule fields for this {@code Holiday}.
     * @return
     */
    JsonObject getRule() {
        return rule;
    }

    /**
     * Returns the date the holiday occurs for the current year.
     *
     * @return a {@code LocalDate} specifying the date the holiday occurs in the current year.
     */
    public final LocalDate getDate() {
        return getDate(LocalDate.now().getYear());
    }

    /**
     * Returns the date the holiday occurs for the specified year.
     * <p>
     * Subclasses must implement this to provide the specific logic they need according
     * to their own rules.
     *
     * @param year The year
     * @return a {@code LocalDate} with the date of the holidays in the specified year.
     */
    public abstract LocalDate getDate(int year);

    /**
     * Returns either the {@link TimeFrame#NEXT} or {@link TimeFrame#LAST} occurrence of the holiday,
     * based on today's date. Uses the provided time zone when determining today's date.
     * <p>
     * If the holiday occurs today, both {@link TimeFrame#NEXT} and {@link TimeFrame#LAST}
     * return today's date.
     *
     * @param timeFrame a {@link TimeFrame} indicating whether to return the
     *                  {@link TimeFrame#NEXT} occurrence of
     *                  the holiday or the {@link TimeFrame#LAST} occurrence of the holiday.
     * @param zoneId    a {@code ZoneId} for a time zone to use when determining today.
     * @return a {@code LocalDate} with either the next or previous occurrence of
     * the holiday, from today.
     */
    public final LocalDate getDate(TimeFrame timeFrame, ZoneId zoneId) {
        // Need to get the holiday date first, as we don't know if it will
        // fall before or after today
        LocalDate today = LocalDate.now(zoneId);
        int year = today.getYear();

        LocalDate holiday = this.getDate(year);

        if (timeFrame == Holiday.TimeFrame.NEXT) {
            // return the holidays if it is on or after today, otherwise get a new one
            if (holiday.isAfter(today) || holiday.isEqual(today)) {
                return holiday;
            } else {
                return this.getDate(++year);
            }
        } else {
            // we want holidays on or before today
            if (holiday.isBefore(today) || holiday.isEqual(today)) {
                return holiday;
            } else {
                return this.getDate(--year);
            }
        }
    }

    /**
     * Enum representing the desired time frame when calculating the date for a
     * {@link Holiday}.
     */
    public enum TimeFrame {
        /**
         * The date returned must be on or before today.
         */
        LAST,
        /**
         * The date returned must be on or after today.
         */
        NEXT
    }

    /**
     * Represents the type of rule needed to calculate the holidays.
     */
    enum RuleType {
        /**
         * Easter has its own unique holidays calculation.
         */
        EASTER,
        /**
         * Identifies a date that occurs a certain number of days before a different date.
         * (For instance, Good Friday is always 2 days before Easter).
         */
        DAYS_BEFORE_HOLIDAY,
        /**
         * Identifies a date that occurs on a specific day in the first full week of a month.
         */
        FIRST_FULL_WEEK_OF_MONTH,
        /**
         * Identifies a date that occurs on a specific day in the last full week of a month.
         */
        LAST_FULL_WEEK_OF_MONTH,
        /**
         * Identifies a date that occurs on the last occurrence of a particular day in a month
         * (such as Memorial Day on the last Monday in May)
         */
        LAST_IN_MONTH,
        /**
         * Identifies a date that occurs on the same date every year.
         */
        STATIC_DATE,
        /**
         * Identifies a date that occurs on a particular day in a particular week of a month
         * (such as President's Day on the 3rd Monday of February).
         */
        WEEK_IN_MONTH
    }
}
