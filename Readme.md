# Holiday Calculation Library

This is a simple Java library for calculating the dates of various holidays. It currently can return the date for several fairly well-known US holidays (and a few more obscure ones). It uses a JSON file to define the rules for the supported holidays, so adding new ones is fairly easy (as long as they don't require new rules).

## Getting a HolidayFactory

Call the `static` method `HolidayFactory.fromDefaults()` to get a new `HolidayFactory` with the default set of US holidays. This gets a new object populated with the holidays defined in `us_holidays_default.json`.

```java
// Get a HolidayFactory using the default holidays
HolidayFactory holidays = HolidayFactory.fromDefaults();
```

I do have additional methods for using other config files, but this is mostly for testing and possible future features. At some point, I might create configuration files for additional holiday sets, such as by locale.

## Getting a Particular Holiday

Once you have a `HolidayFactory`, call `getHoliday()` to get a new `Holiday` object representing a particular holiday. You can specify the holiday you want by string or using a value from the `HolidayFactory.DefaultHolidays` enum.

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

The main goal of these utilities is to calculate the date for a particular holiday. Use one of the `getDate()` methods on the `Holiday` object.

You can get the *next* or *last* occurrence (using today's date as a reference point). You can also specify a particular year. Call `getDate()` with no arguments to get the date for the current year, regardless of whether it is before or after today.

```java
LocalDate date = myHoliday.getDate(Holiday.TimeFrame.NEXT);
System.out.println("The next " +
        myHoliday.getDisplayName() +
        " occurs on " +
        date.toString());

date = myHoliday.getDate(Holiday.TimeFrame.LAST);
System.out.println("The last " +
        myHoliday.getDisplayName() +
        " occurred on " +
        date.toString());

date = myHoliday.getDate(2020);
System.out.println("In 2020, " +
        myHoliday.getDisplayName() +
        " occurs on " +
        date.toString());
```

Output:
    
    The next Presidents Day occurs on 2017-02-20
    The last Presidents Day occurred on 2016-02-15
    In 2020, Presidents Day occurs on 2020-02-17

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

### Common Properties for All Holidays

Each holiday definition must have the following properties:

- `name`
- `displayName`
- `type`
- `rule`

The `type` identifies the type of rule used to calculate the holiday's date. The `rule` property defines fields specific to that particular holiday type. The following rules are currently supported:

### easter

Special rules only used for easter

days_before_holiday      

Holiday that occurs a specific number of days before another holiday.

`holiday` (the other holiday)          
`daysBefore` (the number of days before the other holiday)
`specialDescription` (optional, a string used to provide a human-friendly description of how the holiday is calculated)

### first_full_week_of_month   

Holiday that occurs on a specific day of the week in the *first full week* of a particular month. This is included for completeness, but none of the holidays in the default set use this rule.

`dayOfWeek` (the day of the week)
`month` (the month)                                     

### last_full_week_of_month

Holiday that occurs on a specific day of the week in the *last full week* of a particular month (such as admin professionals day on the Wednesday of the last full week of April).

`dayOfWeek` (the day of the week)
`month` (the month)              

### last_in_month

Holiday that occurs on the last instance of a particular week day of particular month. For example, Memorial Day occurs on the last Monday of May.

`dayOfWeek` (the day of the week)  
`month` (the month)

### static_date

Holiday that occurs on a set date every year. For example, Christmas is always on December 25.

`day` (the day)                                
`month` (the month)                            
`forceWeekday` 

a `boolean` indicating whether the holiday should be moved to either Friday or Monday if it falls on a weekend. Note that this is not yet implemented.

### week_in_month

Holiday that occurs on a particular week day in a particular week of a month. For example, Labor Day is on the first Monday in September

`dayOfWeek` (the day of the week)             
`week` (integer for the week of the month)    
`month` (the month)                           
`afterFirst` (optional, include if the holiday must fall *after* the first instance of a particular day. For example, US Election Day falls on the first Tuesday of November, but only after the first Monday).


## Maven Dependency
(once this is in maven)