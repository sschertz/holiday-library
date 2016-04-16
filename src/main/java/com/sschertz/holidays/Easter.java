package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;

import java.time.LocalDate;

/**
 * Not yet implemented. Needs to be implemented with the rules for calcuating Easter Sunday.
 *
 * For now, just returns today.
 *
 * TODO: Implement Easter Sunday Rules
 */
public class Easter extends Holiday {

    public Easter(JsonObject holidayDefJson) {
        super(holidayDefJson);
    }

    @Override
    public LocalDate getDate(int year) {
        return LocalDate.now();
    }

    @Override
    public String toString() {
        return "Easter calculation is not yet implemented";
    }
}
