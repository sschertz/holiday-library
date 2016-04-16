package com.sschertz.holidays;

import java.util.List;


/**
 * Experimenting with a JSON parser
 */
public class App {

    public static void main(String[] args) {

        System.out.println("Output all the default holidays...");

        HolidayFactory holidays = HolidayFactory.fromDefaults();

        List<Holiday> allHolidays = holidays.getSupportedHolidays();

        for (Holiday holiday : allHolidays) {
            StringBuilder sb = new StringBuilder();
            sb.append(holiday.getDisplayName());
            sb.append(": ");
            sb.append(holiday.getDate());
            sb.append(" (" + holiday.toString() + ").");

            System.out.println(sb.toString());

        }

        System.out.println("Output just the name from the json");

        for (Holiday holiday : allHolidays) {
            System.out.println(holiday.getName());
        }

        System.out.println();
        System.out.println("Output all the test holidays");

        holidays = HolidayFactory.fromTest();
        allHolidays = holidays.getSupportedHolidays();

        for (Holiday holiday : allHolidays) {
            StringBuilder sb = new StringBuilder();
            sb.append(holiday.getDisplayName());
            sb.append(": ");
            sb.append(holiday.getDate());
            sb.append(" (" + holiday.toString() + ").");

            System.out.println(sb.toString());
        }
    }
}
