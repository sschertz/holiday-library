package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.LocalDate;

/**
 * Represents the definition of a particular date, typically used to calculate
 * the date on which a particular holiday falls in a year.
 * Subclasses must implement the methods that do the calculation.
 *
 * Created by schertzs on 2/18/2016.
 *
 */
public abstract class Holiday implements Comparable{

    private String name, displayName;
    private RuleType type;
    private JsonObject rule;

    public static int NUM_DAYS_IN_WEEK = 7;

    /**
     * Compares the displayName of the two objects to sort alphabetically
     *
     * (take a look at this later, to add options for sorting by name or date
     * http://beginnersbook.com/2013/12/java-arraylist-of-object-sort-example-comparable-and-comparator/)
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        String thisString = this.getDisplayName();
        Holiday thatHoliday = (Holiday) o;
        String thatString = thatHoliday.getDisplayName();

        return thisString.compareToIgnoreCase(thatString);
    }

    /**
     * Enum representing the desired time frame for the holidays:
     *
     * LAST: the holidays returned must be before today
     * NEXT: the holidays returned must be after today
     * ANY: Holiday for the provided year, relationship to today doesn't matter.
     */
    public enum TimeFrame{
        LAST, NEXT
    }

    /**
     * Represents the type of rule needed to calculate the holidays.
     */
    public enum RuleType {

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


    public Holiday(JsonObject holidayDefJson){
        // Get all the data out of the JSON rule and put into our fields.
        name = holidayDefJson.get("name").asString();
        displayName =holidayDefJson.get("displayName").asString();
        type = RuleType.valueOf(holidayDefJson.get("type").asString().toUpperCase());
        rule = holidayDefJson.get("rule").asObject();
    }

    public String getName(){
        return name;
    }

    public String getDisplayName(){
        return displayName;
    }

    public RuleType getType(){
        return type;
    }

    public JsonObject getRule() {
        return rule;
    }

    /**
     * Returns the date the holiday occurs for the current year.
     *
     * @return a {@code LocalDate} specifying the date the holiday occurs in the current year.
     */
    public final LocalDate getDate(){
        return getDate(LocalDate.now().getYear());
    }

    /**
     * Returns the date the holidays occurs for the specified year.
     *
     * Subclasses must implement this to provide the specific logic they need according
     * to their own rules.
     *
     * @param year The year
     * @return a LocalDate with the date of the holidays in the specified year.
     */
    public abstract LocalDate getDate(int year);

    /**
     * Returns either the {@link .TimeFrame.NEXT} or {@link .TimeFrame.LAST} occurrence of the holiday,
     * based on today's date.
     *
     * @param timeFrame a {@link TimeFrame} indicating whether to return the NEXT occurrence of
     *                  the holiday or the LAST occurrence of the holiday.
     * @return a LocalDate with the date of the holiday in the specified year.
     */
    public final LocalDate getDate(TimeFrame timeFrame){
        // Need to get the holiday date first, as we don't know if it will
        // fall before or after today
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        LocalDate holiday = this.getDate(year);

        if (timeFrame == Holiday.TimeFrame.NEXT){
            // return the holidays if it on or after today, otherwise get a new one
            if (holiday.isAfter(today)){
                return holiday;
            } else {
                return this.getDate(++year);
            }
        } else {
            // we want holidays before today
            if (holiday.isBefore(today)){
                return holiday;
            } else {
                return this.getDate(--year);
            }
        }
    }



}
