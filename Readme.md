# Holiday Calculation Library

This is a simple Java library for calculating the dates of various holidays. I created this for a personal project where I needed to easily get the dates of various US holidays.

It currently can return the date for several fairly well-known US holidays (and a few more obscure ones). It uses a JSON file to define the rules for the supported holidays, so adding new ones is fairly easy (as long as they don't require logic I haven't yet implemented).

## Getting a HolidayFactory

Call the `static` method `HolidayFactory.fromDefaults()` to get a new `HolidayFactory` with the default set of US holidays. This gets a new object that can return any of the holidays defined in `us_holidays_default.json`.

```java
// Get a HolidayFactory using the default holidays
HolidayFactory holidays = HolidayFactory.fromDefaults();
```

I do have additional methods for using other configuration files, but this is mostly for testing and possible future features. At some point, I might create configuration files for additional holiday sets, such as by locale.

## Getting a Particular Holiday

Once you have a `HolidayFactory`, call `getHoliday()` to get a new `Holiday` object representing a particular holiday. You can specify the holiday you want by string or using a value from the `HolidayFactory.DefaultHolidays` enumeration.

When using a string, it must match exactly the name of the holiday as defined in the JSON file. Use `isHolidayDefined()` to make sure the holiday exists first.

```java
if (holidays.isHolidayDefined("labor day")){
    Holiday myHoliday = holidays.getHoliday("labor day");
    LocalDate myHolidayDate = myHoliday.getDate(2017);
    System.out.println("In 2017, " +
            myHoliday.getDisplayName() +
            " occurs on: " +
            myHolidayDate.toString());
}
```

Output:

    In 2017, Labor Day occurs on: 2017-09-04

The `HolidayFactory.DefaultHolidays` enumeration contains constants for all of the holidays currently defined in the JSON file. This can be a more convenient way to get a specific holiday without the need to match the string name.

```java
Holiday myHoliday = holidays.getHoliday(
    HolidayFactory.DefaultHolidays.PRESIDENTS_DAY);
```

You can also call `getSupportedHolidays` to get a `List` containing a `Holiday` object for each supported holiday.

## Accessing Data about a Specific Holiday

Use one of the `getDate()` methods on the `Holiday` object:

- Pass `getDate()` a value from the `Holiday.Timeframe` enumeration and a time zone to get the `NEXT` or `NEXT` occurrence. This uses today's date as a reference point. The time zone is needed to ensure that "today" is the date you expect.
- Pass `getDate()` a specific year to get the date of the holiday in that year.
- Call `getDate()` with no arguments to get the date for the current year, regardless of whether it is before or after today.

```java
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
```

Output:

``` 
The next Presidents Day occurs on 2017-02-20
The last Presidents Day occurred on 2016-02-15
In 2020, Presidents Day occurs on 2020-02-17
In the current year of 2016, Presidents Day falls on 2016-02-15
```

## DateUtilities Class

This class contains some static methods for various date manipulations useful when calculating holidays, such as getting the first or last day of a month, getting the third Monday in a particular month, and so on.

## Holiday Definition Files

The library uses a `JSON` file to define the rules for the supported holidays, so it is fairly easy to add additional holidays without changing the code. The `JSON` file lives in the `resources` folder. I currently have two:

- `us_holidays_default.json`: the main file, defining the default holidays. This is used when calling `HolidayFactory.fromDefaults()`.
- `test_holidays.json`: This contains some fake holidays I created for testing. It is used when calling `HolidayFactory.fromTest()`.

I may eventually add the ability to load a file from the file system rather than using resources.

## JSON File Format

The file includes some top-level properties for meta-data about the file (`name`, `version`, etc.). The actual holidays are defined within the `supportedHolidays` property. 

```json
{
  "name": "Default Supported Holidays",
  "version": "1.0",
  "lastUpdated": "2016-02-18",
  "locale": "US",
  "supportedHolidays": {
    "christmas": {
      "name": "christmas",
      "displayName": "Christmas Day",
      "type": "static_date",
      "rule": {
        "month": "december",
        "day": 25,
        "forceWeekday": false
      }
    },
    "labor day": {
      "name": "labor day",
      "displayName": "Labor Day",
      "type": "week_in_month",
      "rule": {
        "dayOfWeek": "monday",
        "week": 1,
        "month": "september"
      }
    }
  }
}
```

### Properties Used to Define a Holiday

Each holiday definition must have the following properties:

- `name`
- `displayName`
- `type`
- `rule`

The `type` identifies the type of rule used to calculate the holiday's date. The `rule` property is an object that defines any fields specific to that particular holiday type. 

The following sections summarize the valid `type` values and the properties that should be included within the `rule` object for each `type`.

### easter

Special rules only used for easter. This type has no type-specific rules, so the `rule` property should be set to an empty object:

```json
{
  "easter": {
    "name": "easter",
    "displayName": "Easter Sunday",
    "type": "easter",
    "rule": {}
  }
}
```

### days_before_holiday      

Holiday that occurs a specific number of days before another holiday.

Rule fields:

- `holiday`: the other holiday. This holiday must be defined in the same configuration file.
- `daysBefore`: the number of days before the other holiday.
- `specialDescription`: (optional) a string used to provide a human-friendly description of how the holiday is calculated.

### first_full_week_of_month   

Holiday that occurs on a specific day of the week in the *first full week* of a particular month. This is included for completeness, but none of the holidays in the default set use this rule.

Rule fields:

- `dayOfWeek`: the day of the week, provided as a string name such as "monday".
- `month`: the month, provided as a string such as "september".                                     

### last_full_week_of_month

Holiday that occurs on a specific day of the week in the *last full week* of a particular month (such as admin professionals day on the Wednesday of the last full week of April).

Rule fields:

- `dayOfWeek`: the day of the week, provided as a string name such as "monday".
- `month`: the month, provided as a string such as "september".

### last_in_month

Holiday that occurs on the last instance of a particular week day of particular month. For example, Memorial Day occurs on the last Monday of May.

Rule fields:

- `dayOfWeek`: the day of the week, provided as a string name such as "monday".
- `month`: the month, provided as a string such as "september".

### static_date

Holiday that occurs on a set date every year. For example, Christmas is always on December 25. 

Rule fields:

- `day`: the day, provided as an integer.          
- `month`: the month, provided as a string such as "september".
- `forceWeekday`: (optional) a `boolean` indicating whether the holiday should be moved to either Friday or Monday if it falls on a weekend. Defaults to `false` if not provided. *Note that this is not yet implemented*.

### week_in_month

Holiday that occurs on a particular week day in a particular week of a month. For example, Labor Day is on the first Monday in September.

- `dayOfWeek`: the day of the week, provided as a string name such as "monday".
- `week`: integer for the week of the month.
- `month`: the month, provided as a string such as "september".
- `afterFirst`: (optional) the day of the week that the holiday must fall after. For example, US Election Day falls on the first Tuesday of November, but only after the first Monday. So the configuration for this holiday would include `"afterFirst": "monday"`.

## HolidaySample Project

See the provided `HolidaySample` project for sample code calling the library.

## Future Updates

This isn't really finished...some potential updates:

- [ ] Finish implementing the logic for forcing static date holidays to either Friday or Monday. This would be useful for calculating the dates those are observed for the purposes of days off or bank holidays.
- [ ] Add additional "observed" holidays to the main config json.
- [ ] More sorting options when getting a list of `Holiday` objects.
- [ ] Add support for more holidays.
- [ ] Add support for holidays outside the US.
- [ ] Add ability to read the holiday configuration from a passed-in filename.

I also intend to improve the JavaDoc a bit, especially for consistency.
