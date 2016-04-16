package com.sschertz.holidays;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;


/**
 * Experimenting with a JSON parser
 */
public class App {

    public static void main(String[] args) {

        System.out.println("Test with the new factory method");

        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);

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


    }

}
