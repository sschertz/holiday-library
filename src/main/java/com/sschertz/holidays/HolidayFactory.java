package com.sschertz.holidays;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.JsonObject.Member;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates {@link Holiday} objects based on rules for calculating different holidays. The rules for the
 * holidays are pre-defined.
 *
 * Use {@link .fromDefaults()} to instantiate this class with a default set of holidays (currently well-known
 * holidays recognized in the United States).
 *
 * Once you have a {@code HolidayFactory}, call {@link .getHoliday()} to retrieve a specific holiday.
 *
 * Use {@link .getSupportedHolidays()} to get a {@code List} of all holidays this {@code HolidayFactory} can return.
 */
public class HolidayFactory {

    private String version, lastUpdated, locale, name;
    private JsonObject supportedHolidaysJson;

    public static String RESOURCE_DEFAULT = "us_holidays_default.json";
    public static String RESOURCE_TEST = "test_holidays.json";

    public enum DefaultHolidays {
        /**
         * Enumeration of all supported holidays in the default set (obtained with {@link .fromDefaults}.
         * This can be used for convenience when retrieving a specific known holiday from the set.
         *
         * The {@code friendlyName} matches the name used in the JSON files.
         */
        CHRISTMAS("christmas"),
        CHRISTMAS_OBSERVED("christmas observed"),
        CHRISTMAS_EVE("christmas eve"),
        LABOR_DAY("labor day"),
        PRESIDENTS_DAY("presidents day"),
        US_ELECTION_DAY("us election day"),
        MEMORIAL_DAY("memorial day"),
        ADMINISTRATIVE_PROFESSIONALS_DAY("administrative professionals day"),
        EASTER("easter"),
        PALM_SUNDAY("palm sunday"),
        GOOD_FRIDAY("good friday"),
        INDEPENDENCE_DAY("independence day");

        private String friendlyName;

        DefaultHolidays(String friendlyName) {
            this.friendlyName = friendlyName;

        }

        public String getFriendlyName() {
            return friendlyName;
        }
    }


    /**
     * Private constructor. Parses the provided {@code reader} and returns a new {@code HolidayFactory}.
     *
     * @param reader
     */
    private HolidayFactory(Reader reader) {

        JsonObject configFile;
        try {
            configFile = Json.parse(reader).asObject();

            name = configFile.get("name").asString();
            version = configFile.get("version").asString();
            lastUpdated = configFile.get("lastUpdated").asString();
            locale = configFile.get("locale").asString();
            supportedHolidaysJson = configFile.get("supportedHolidays").asObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private JsonObject getHolidayJson(String holiday) {

        // get the JsonValue for the requested holiday.
        JsonValue result = supportedHolidaysJson.get(holiday);

        return (result != null) ? result.asObject() : null;

    }

    /**
     * Returns a new {@code HolidayFactory} using the default set of holidays (generally
     * well known holidays recognized in the United States)
     *
     * @return a new {@code HolidayFactory} configured with default US holidays.
     */
    public static HolidayFactory fromDefaults() {
        return fromResource(RESOURCE_DEFAULT);
    }

    /**
     * Returns a new {@code HolidayFactory} using a set of test holidays. These demonstrate all of the
     * currently implemented rules for calculating holidays.
     *
     * @return a new {@code HolidayFactory} configured with test holidays.
     */
    public static HolidayFactory fromTest() { return fromResource(RESOURCE_TEST);}

    /**
     * Returns a new {@code HolidayFactory} using the set of holidays defined in the specified
     * {@code resource}. Returns {@code null} if the resource cannot be found.
     *
     * @param resource
     * @return
     */
    public static HolidayFactory fromResource(String resource){

        InputStream is = HolidayFactory.class.getClassLoader().getResourceAsStream(resource);
        InputStreamReader reader;

        if (is != null) {
            try {
                reader = new InputStreamReader(is, "UTF-8");
                return new HolidayFactory(reader);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // Failed to get the resource
        return null;
    }

    /**
     * Returns a new {@code HolidayFactory} using the set of holidays defined in the specified
     * {@code filename}. Returns {@code null} if the file cannot be found.
     *
     * NOT YET IMPLEMENTED!
     *
     * @param filename
     * @return
     */
    public static HolidayFactory fromFile(String filename){
        //code to read in a file here, get a FileReader, then call
        // the HolidayFactory constructor to do the parsing.

        return null;
    }

    /**
     * Tests whether this {@code HolidayFactory}contains the holiday identified by the
     * provided {@code holiday} string.
     *
     * @param holiday
     * @return {@code true} if the specified {@code holiday} is defined; {@code false} otherwise.
     */
    public boolean isHolidayDefined(String holiday) {

        JsonValue result = supportedHolidaysJson.get(holiday);
        return result != null;
    }


    /**
     * Returns a {@code Holiday} object for the holiday identified by the
     * {@code holiday} string. The {@code Holiday} returned contains
     * basic information (such as a display name) and the specific rules for how the
     * holiday should be calculated.
     *
     * @param holiday a {@code String} with the name of the holiday to retrieve.
     * @return a {@link Holiday} object for the specified holiday; {@code null} if the
     * requested holiday is not defined.
     */
    public Holiday getHoliday(String holiday) {

        JsonObject jsonRule = getHolidayJson(holiday);
        if (jsonRule == null) {
            return null;
        }

        // Get the ruleType so we know which object to create
        Holiday.RuleType ruleType = Holiday.RuleType.valueOf(jsonRule.get("type").asString().toUpperCase());

        switch (ruleType) {
            case STATIC_DATE:
                return new StaticDate(jsonRule);
            case WEEK_IN_MONTH:
                return new WeekInMonth(jsonRule);
            case LAST_IN_MONTH:
                return new LastInMonth(jsonRule);
            case LAST_FULL_WEEK_OF_MONTH:
                return new LastFullWeekOfMonth(jsonRule);
            case FIRST_FULL_WEEK_OF_MONTH:
                return new FirstFullWeekOfMonth(jsonRule);
            case EASTER:
                return new Easter(jsonRule);
            case DAYS_BEFORE_HOLIDAY:
                return new DaysBeforeHoliday(jsonRule);
            default:
                System.out.println("Fell all the way into default.");
                break;
        }
        // this should never happen once the switch statement is implemented.
        // if we passed a bad ruleType that doesn't match, a runtime
        // exception should happen before we even start the switch.
        return null;
    }

    /**
     * Returns a {@code Holiday} object for the holiday identified by the
     * {@link DefaultHolidays} enum value. The {@code Holiday} returned contains
     * basic information (such as a display name) and the specific rules for how the
     * holiday should be calculated.
     *
     * The {@link DefaultHolidays} enum currently supports the set of holidays retrieved using
     * {@link .fromDefaults()}. If this {@code HolidayFactory} has been initialized with
     * a different set of holidays, this method throws an {@link IllegalArgumentException}.
     *
     * @param holiday a {@code HOLIDAY} for the holiday to retrieve.
     * @return a {@link Holiday} object for the specified holiday; {@code null} if the
     * requested holiday is not defined.
     */
    public Holiday getHoliday(DefaultHolidays holiday){
        if (this.getName().equals("Default Supported Holidays")){
            // call getHoliday with the string name for this holiday
            return this.getHoliday(holiday.getFriendlyName());
        } else{
            throw new IllegalArgumentException("Not using the default supported holiday list.");
        }

    }

    public String getVersion() {
        return version;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getLocale() {
        return locale;
    }

    /**
     * Returns a List of all holidays supported by this {@code HolidayFactory}, sorted alphabetically.
     *
     * @return
     */
    public List<Holiday> getSupportedHolidays() {

        List<Holiday> holidayList = new ArrayList<>();
        for (Member holiday : supportedHolidaysJson) {
            holidayList.add(getHoliday(holiday.getName()));
        }
        Collections.sort(holidayList);

        return holidayList;
    }

    /**
     * Returns a string containing a comma-separated all holidays supported by
     * this {@code HolidayFactory}, sorted alphabetically.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + ": ");
        List<Holiday> holidayList = this.getSupportedHolidays();
        for (Holiday holiday : holidayList) {
            sb.append(holiday.getDisplayName());
            sb.append(", ");
        }
        int indexLastComma = sb.toString().lastIndexOf(", ");
        return sb.toString().substring(0, indexLastComma);
    }

    public String getName() {
        return name;
    }
}
