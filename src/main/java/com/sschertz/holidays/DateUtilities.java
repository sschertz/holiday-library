package com.sschertz.holidays;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

/**
 * Class for some date-related helper methods. These are primarily for use within my
 * {@link HolidayFactory} class, but could potentially be used for other purposes.
 *
 * Created by saraschertz on 2/21/16.
 */
public class DateUtilities {

    private static int NUM_DAYS_IN_WEEK = 7;


    /**
     * Returns a {@code LocalDate} for the first day of the specified {@code month} in the specified
     * {@code year}.
     *
     * @param year the year
     * @param month the month
     * @return a {@code LocalDate} for the first day of the {@code month} and {@code year}.
     */
    public static LocalDate getFirstDayOfMonth(int year, Month month){
        return LocalDate.of(year, month, 1);
    }

    public static LocalDate getLastDayOfMonth(int year, Month month){
        return LocalDate.of(year, month, month.length(Year.of(year).isLeap()));
    }

    /**
     * Returns the {@code LocalDate} of the date that starts the last FULL WEEK of the
     * specified {@code month} for the provided {@code year}.
     *
     * This considers Sunday as the first day of the week, so the returned date will
     * always be a Sunday.
     *
     * @param year the year
     * @param month the month
     * @return a {@code LocalDate} for the Sunday that starts the last full week of the {@code month}.
     */
    public static LocalDate getLastFullWeekOfMonth(int year, Month month){

        // get the last Sunday of the month
        LocalDate lastStartOfWeek = getLastSpecifiedDayInMonth(year, month, DayOfWeek.SUNDAY);
        int lastDayOfMonth = month.length(Year.of(year).isLeap());

        // are there at least 6 more days?
        if (lastStartOfWeek.getDayOfMonth() + 6 <= lastDayOfMonth){
            return lastStartOfWeek;
        } else {
            // we need to go back one week
            return lastStartOfWeek.minusWeeks(1);
        }
    }

    /**
     * Returns the {@code LocalDate} of the date that starts the first FULL WEEK of the
     * specified {@code month} for the provided {@code year}.
     *
     * This considers Sunday as the first day of the week, so the returned date will
     * always be a Sunday.
     *
     * @param year the year
     * @param month the month
     * @return a {@code LocalDate} for the Sunday that starts the first full week of the {@code month}.
     */
    public static LocalDate getFirstFullWeekOfMonth(int year, Month month){
        // get the first Sunday of the month
        return getSpecifiedDayInWeekOfMonth(year, month, 1, DayOfWeek.SUNDAY);

    }


    /**
     * Returns a {@code LocalDate} for the specified {@code dayOfWeek} in the week that begins
     * with the specified date ({@code weekStartDate}).
     *
     * For example, if {@code weekStartDate} is 2016-02-07 and {@code dayOfWeek} is WEDNESDAY,
     * this method would return 2016-02-10 (the Wednesday in the week starting 2016-02-07).
     *
     *
     * @param weekStartDate
     * @param dayOfWeek
     * @return
     */
    public static LocalDate getSpecifiedDayInWeek(LocalDate weekStartDate, DayOfWeek dayOfWeek){
        DayOfWeek startDay = weekStartDate.getDayOfWeek();

        if (dayOfWeek == startDay) return weekStartDate;

        int daysFromStartToTargetDay = (startDay.getValue() - NUM_DAYS_IN_WEEK) + dayOfWeek.getValue();

        return weekStartDate.plusDays(daysFromStartToTargetDay);

    }

    /**
     * Returns the {@code LocalDate} that corresponds to the occurrence of {@code dayOfWeek} in the specified
     * {@code week} of the {@code month}.
     *
     * For example, if passed February, 3, and Monday, this would return the third Monday in February for the
     * provided {@code year}
     *
     * @param year
     * @param month
     * @param week
     * @param dayOfWeek
     * @return
     */
    public static LocalDate getSpecifiedDayInWeekOfMonth(int year, Month month, int week, DayOfWeek dayOfWeek){
        int baseDaysToAdd = (week - 1) * NUM_DAYS_IN_WEEK;
        int daysUntilTarget;

        // Get the date and DayOfWeek of the first day of the specified month
        LocalDate firstDayOfMonth = DateUtilities.getFirstDayOfMonth(year, month);
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();

        // determine how many days to add to the first day of the month to get the requested dayOfWeek
        // in the requested week.
        if (firstDayOfWeek.getValue() > dayOfWeek.getValue()){
            // example, date is on 3rd monday of the month. Monday = 1. First day of the month falls on Tuesday (2)
            daysUntilTarget = NUM_DAYS_IN_WEEK - (firstDayOfWeek.getValue() - dayOfWeek.getValue());

        } else if (firstDayOfWeek.getValue() < dayOfWeek.getValue()){
            daysUntilTarget = Math.abs((firstDayOfWeek.getValue() - dayOfWeek.getValue()));
        } else
            daysUntilTarget = 0;

        // Return our date.
        return firstDayOfMonth.plusDays(daysUntilTarget+baseDaysToAdd);
    }


    /**
     * Get the last specified {@code dayOfWeek} in the provided {@code month}.
     * For example, get the last Monday in May.
     *
     *
     * @param year
     * @param month
     * @param dayOfWeek
     * @return
     */
    public static LocalDate getLastSpecifiedDayInMonth(int year, Month month, DayOfWeek dayOfWeek){

        // Get the last dayOfWeek of the provided month (currently does not account for leap year in Feb.)
        int daysToSubtract;
        LocalDate lastDayOfMonth = getLastDayOfMonth(year,month);

        // Next, how many days past dayOfWeek is lastDayOfMonth?
        if (lastDayOfMonth.getDayOfWeek().getValue() - dayOfWeek.getValue() >= 0){
            daysToSubtract = lastDayOfMonth.getDayOfWeek().getValue() - dayOfWeek.getValue();
        } else {
            daysToSubtract =
                    NUM_DAYS_IN_WEEK - Math.abs(lastDayOfMonth.getDayOfWeek().getValue() - dayOfWeek.getValue());
        }

        return lastDayOfMonth.minusDays(daysToSubtract);

    }



}
