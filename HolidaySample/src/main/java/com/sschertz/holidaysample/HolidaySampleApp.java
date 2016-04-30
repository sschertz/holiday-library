package com.sschertz.holidaysample;

import com.sschertz.holidays.Holiday;
import com.sschertz.holidays.HolidayFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class HolidaySampleApp {

    /**
     * Simple test app to demonstrate calculating various holidays using HolidayLibrary.
     */

    public static void main(String[] args) {

        // Get a HolidayFactory using the default holidays
        HolidayFactory holidays = HolidayFactory.fromDefaults();

        // Get a specific holiday by string - verify it exists first
        System.out.println("Getting a holiday by string");
        if (holidays.isHolidayDefined("labor day")){
            Holiday myHoliday = holidays.getHoliday("labor day");
            LocalDate myHolidayDate = myHoliday.getDate(2017);
            System.out.println("In 2017, " +
                    myHoliday.getDisplayName() +
                    " occurs on: " +
                    myHolidayDate.toString());
        }

        System.out.println();

        // Get a holiday using the enum. This only works when using the default
        // set of holidays (retrieved via HolidayFactory.fromDefaults()).

        Holiday myHoliday = holidays.getHoliday(HolidayFactory.DefaultHolidays.PRESIDENTS_DAY);

        /*
            Once you have a holiday, you can get the NEXT occurrence, the LAST
            occurrence, the occurrence for the current year, or the occurrence
            in a specific year. NEXT and LAST use today's date as a reference point.
         */
        System.out.println("Get the dates for a holiday");
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");

        LocalDate date = myHoliday.getDate(Holiday.TimeFrame.NEXT, zoneId);
        System.out.println("The next " +
                myHoliday.getDisplayName() +
                " occurs on " +
                date.toString());

        date = myHoliday.getDate(Holiday.TimeFrame.LAST, zoneId);
        System.out.println("The last " +
                myHoliday.getDisplayName() +
                " occurred on " +
                date.toString());

        date = myHoliday.getDate(2020);
        System.out.println("In 2020, " +
                myHoliday.getDisplayName() +
                " occurs on " +
                date.toString());

        date = myHoliday.getDate();
        System.out.println("In the current year of " +
                LocalDate.now().getYear() + ", " +
                myHoliday.getDisplayName() +
                " falls on " +
                date.toString());

        System.out.println();

        System.out.println("Output all the default holidays...");
        // Get a list of all the holidays the factory can handle
        List<Holiday> allHolidays = holidays.getSupportedHolidays();

        String outputString;

        // Output the date for each holiday in the list for the current year.
        for (Holiday holiday : allHolidays) {
            outputString = holiday.getDisplayName() +
                    ": " +
                    holiday.getDate() +
                    " (" + holiday.toString() + ").";
            System.out.println(outputString);
        }

        System.out.println();
        System.out.println("Get Easter 2010 - 2020 to verify accuracy...");

        // Use the existing factory to retrieve an object for a particular holiday,
        // then get the date of that holiday for specific years.
        Holiday easter = holidays.getHoliday(HolidayFactory.DefaultHolidays.EASTER);
        for (int year = 2010; year < 2021; year++) {
            System.out.println(year + ": " + easter.getDate(year));
        }





        System.out.println();
        System.out.println("Testing for the existence of a holiday definition");

        if (holidays.isHolidayDefined("not defined")){
            System.out.println("This should never output");
        } else
            System.out.println("That holiday is not defined.");

        System.out.println();
        System.out.println("Output all the test holidays...");

        holidays = HolidayFactory.fromTest();
        allHolidays = holidays.getSupportedHolidays();

        for (Holiday holiday : allHolidays) {
            String sb = holiday.getDisplayName() +
                    ": " +
                    holiday.getDate() +
                    " (" + holiday.toString() + ").";

            System.out.println(sb);
        }


    }


}
