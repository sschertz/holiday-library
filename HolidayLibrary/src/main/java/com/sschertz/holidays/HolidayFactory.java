package com.sschertz.holidays;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates {@link Holiday} objects based on rules for calculating different holidays. The rules for the
 * holidays are pre-defined.
 * <p>
 * Use {@link #fromDefaults()} to instantiate this class with a default set of holidays (currently well-known
 * holidays recognized in the United States).
 * <p>
 * Once you have a {@code HolidayFactory}, call {@link #getHoliday(String)} to retrieve a specific holiday.
 * <p>
 * Use {@link #getSupportedHolidays()} to get a {@code List} of all holidays this {@code HolidayFactory} can return.
 */
public class HolidayFactory {

    private static String RESOURCE_DEFAULT = "us_holidays_default.json";
    private static String RESOURCE_TEST = "test_holidays.json";
    private String version, lastUpdated, locale, name;
    private JsonObject supportedHolidaysJson;

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

    /**
     * Returns a new {@code HolidayFactory} using the default set of holidays (generally
     * well known holidays recognized in the United States)
     *
     * @return a new {@code HolidayFactory} that can return any of the default US holidays.
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
    public static HolidayFactory fromTest() {
        return fromResource(RESOURCE_TEST);
    }

    /**
     * Returns a new {@code HolidayFactory} using the set of holidays defined in the specified
     * {@code resource}. Returns {@code null} if the resource cannot be found.
     *
     * @param resource name of the resource to use for the configuration file.
     * @return a new {@code HolidayFactory} that can return holidays defined in the specified resource.
     */
    public static HolidayFactory fromResource(String resource) {

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
     * NOT YET IMPLEMENTED! Returns a new {@code HolidayFactory} using the set of holidays defined in the specified
     * {@code filename}. Returns {@code null} if the file cannot be found.
     * <p>
     * <p>
     * TODO: Add ability to specify a random filename for the JSON def file
     *
     * @param filename
     * @return
     */
    @SuppressWarnings("UnusedParameters")
    public static HolidayFactory fromFile(String filename) {
        // code to read in a file here, get a FileReader, then call
        // the HolidayFactory constructor to do the parsing.

        return null;
    }


    /**
     * Tests whether this {@code HolidayFactory} contains a definition for the holiday
     * identified by the provided {@code holidayName} string.
     *
     * @param holidayName the {@code String} holiday name to check.
     * @return {@code true} if the specified {@code holiday} is defined; {@code false} otherwise.
     */
    public boolean isHolidayDefined(String holidayName) {

        JsonValue result = supportedHolidaysJson.get(holidayName);
        return result != null;
    }

    /**
     * Returns a {@link Holiday} object for the holiday identified by the
     * {@code holidayName} string. The {@link Holiday} returned contains
     * basic information (such as a display name) and the specific rules for how the
     * holiday should be calculated.
     *
     * @param holidayName a {@code String} with the name of the holiday to retrieve.
     * @return a {@link Holiday} object for the specified holiday; {@code null} if the
     * requested holiday is not defined in this {@code HolidayFactory}.
     */
    public Holiday getHoliday(String holidayName) {

        JsonObject jsonRule = getHolidayJson(holidayName);
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
                // This is a special case that depends on another holiday.
                // We need to make sure that the other holiday definition exists
                // within the set of holidays supported by this HolidayFactory

                JsonObject otherHolidayJson = jsonRule.get("rule").asObject();
                String otherHolidayString = otherHolidayJson.get("holiday").asString();
                if (this.isHolidayDefined(otherHolidayString)) {
                    return new DaysBeforeHoliday(jsonRule, this.getHoliday(otherHolidayString));
                } else {
                    // the holiday definition is invalid. It depends on a holiday
                    // that has not been defined.
                    throw new IllegalArgumentException("Holiday rule definition is invalid");
                }
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
     * Returns a {@link Holiday} object for the holiday identified by the
     * {@link DefaultHolidays} enum value. The {@link Holiday} returned contains
     * basic information (such as a display name) and the specific rules for how the
     * holiday should be calculated.
     * <p>
     * The {@link DefaultHolidays} enum currently supports the set of holidays retrieved using
     * {@link #fromDefaults()}. If this {@code HolidayFactory} has been initialized with
     * a different set of holidays, this method throws an {@link IllegalArgumentException}.
     *
     * @param holiday a value from {@link DefaultHolidays} for the holiday to retrieve.
     * @return a {@link Holiday} object for the specified holiday; {@code null} if the
     * requested holiday is not defined.
     */
    public Holiday getHoliday(DefaultHolidays holiday) {
        if (this.getName().equals("Default Supported Holidays")) {
            // call getHoliday with the string name for this holiday
            return this.getHoliday(holiday.getFriendlyName());
        } else {
            throw new IllegalArgumentException("Not using the default supported holiday list.");
        }

    }

    /**
     * Gets a version number for the holiday configuration file.
     * <p>
     * (This is not currently used for anything)
     *
     * @return a string for the version number
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets a string specifying when the holiday config file was last updated.
     * <p>
     * (This is not currently used for anything)
     *
     * @return a string for the last updated date.
     */
    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Gets a string specifying the locale for the holiday config file.
     * <p>
     * Note that this currently does not mean anything -- the factory does
     * not support multiple locales.
     *
     * @return a string containing the locale for the config file.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Gets a {@code List} of {@link Holiday} objects representing all the holidays
     * supported by this {@code HolidayFactory}, sorted alphabetically.
     *
     * @return a {@code List} of {@link Holiday} objects, sorted alphabetically.
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
     * Returns a {@code String} containing a comma-separated list of all holidays supported by
     * this {@code HolidayFactory}, sorted alphabetically. This uses the display name for
     * the holiday name.
     *
     * @return a comma-separated {@code String} of supported holidays, sorted alphabetically.
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

    /**
     * Returns the name of the configuration file used for this {@code HolidayFactory}.
     *
     * @return the name of the configuration file.
     */
    public String getName() {
        return name;
    }

    private JsonObject getHolidayJson(String holiday) {

        // get the JsonValue for the requested holiday.
        JsonValue result = supportedHolidaysJson.get(holiday);

        return (result != null) ? result.asObject() : null;
    }

    /**
     * Enumeration of all supported holidays in the default set (obtained with {@link #fromDefaults}).
     * This can be used for convenience when retrieving a specific known holiday from the set.
     * <p>
     * The {@link DefaultHolidays#getFriendlyName()} method returns the name used to identify
     * the holiday in the JSON files.
     * <p>
     */
    public enum DefaultHolidays {
        ADMINISTRATIVE_PROFESSIONALS_DAY("administrative professionals day"),
        ALL_SOULS_DAY("all souls day"),
        ARBOR_DAY("arbor day"),
        ARMED_FORCES_DAY("armed forces day"),
        BLACK_FRIDAY("black friday"),
        CHRISTMAS("christmas"),
        CHRISTMAS_OBSERVED("christmas observed"),
        CHRISTMAS_EVE("christmas eve"),
        D_DAY("d-day"),
        DAY_OF_THE_DEAD("day of the dead"),
        EARTH_DAY("earth day"),
        EASTER("easter"),
        ELECTION_DAY_US("election day us"),
        FATHERS_DAY("fathers day"),
        FLAG_DAY("flag day"),
        GOOD_FRIDAY("good friday"),
        GROUNDHOG_DAY("groundhog day"),
        HALLOWEEN("halloween"),
        HARVEY_MILK_DAY("harvey milk day"),
        INDEPENDENCE_DAY("independence day"),
        INDIGENOUS_PEOPLES_DAY("indigenous peoples day"),
        JUNETEENTH("juneteenth"),
        LABOR_DAY("labor day"),
        MARTIN_LUTHER_KING_DAY("martin luther king day"),
        MEMORIAL_DAY("memorial day"),
        MOTHERS_DAY("mothers day"),
        NATIVE_AMERICANS_DAY("native americans day"),
        NEW_YEARS_DAY("new years day"),
        NEW_YEARS_EVE("new years eve"),
        PALM_SUNDAY("palm sunday"),
        PRESIDENTS_DAY("presidents day"),
        ST_PATRICKS_DAY("st patricks day"),
        THANKSGIVING_DAY("thanksgiving day"),
        VETERANS_DAY("veterans day");

        private String friendlyName;

        DefaultHolidays(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public String getFriendlyName() {
            return friendlyName;
        }
    }
}
