package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.LocalDate;

/**
 * Easter Sunday is the first Sunday after the first full moon of spring.
 *
 * Use the algorithm invented by the mathematician Carl Friedrich Gauss in 1800
 */
class Easter extends Holiday {

    public Easter(JsonObject holidayDefJson) {
        // No holiday-specific rules, we only need the year.
        super(holidayDefJson);
    }

    @Override
    public LocalDate getDate(int year) {
        // just use year for year
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int g = (8 * b + 13) / 25;
        int h = (19 * a + b - d - g + 15) % 30;
        int j = c / 4;
        int k = c % 4;
        int m = (a + 11 * h) / 319;
        int r = (2 * e + 2 * j - k - h + m + 32) % 7;
        int n = (h - m + r + 90) / 25;
        int p = (h - m + r + n + 19) % 32;

        // supposedly after all that, the month is n and the day is p.

        return LocalDate.of(year, n, p);

    }

    @Override
    public String toString() {
        return "Easter Sunday occurs on the first Sunday after the first full moon of spring.";
    }
}
