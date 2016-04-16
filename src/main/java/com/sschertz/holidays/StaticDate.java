package com.sschertz.holidays;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Created by schertzs on 2/18/2016.
 */
final class StaticDate extends Holiday {

    private Month month;
    private int day;
    private boolean forceWeekday = false;

    public StaticDate(JsonObject holidayDefJson){
        super(holidayDefJson);

        // Set the rule-specific fields for this subclass
        month = Month.valueOf(getRule().get("month").asString().toUpperCase());
        day = getRule().get("day").asInt();

        JsonValue weekday = holidayDefJson.get("rule").asObject().get("forceWeekday");
        forceWeekday = (weekday != null) && weekday.asBoolean();
    }

    @Override
    public LocalDate getDate(int year) {
        //TODO: Implement handling of the forceWeekday rule!

        return LocalDate.of(year, month, day);

    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.getDisplayName());
        sb.append(" occurs on ");
        sb.append(this.month.getDisplayName(TextStyle.FULL, Locale.US));
        sb.append(" ");
        sb.append(this.day);
        sb.append(" every year. ");
        if (forceWeekday == true){
            sb.append("If it falls on a weekend, the holidays is observed on either the Friday before or Monday after");
        }

        return sb.toString();
    }

}
