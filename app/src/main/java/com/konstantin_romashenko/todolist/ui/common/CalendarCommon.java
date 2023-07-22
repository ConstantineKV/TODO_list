package com.konstantin_romashenko.todolist.ui.common;

import java.util.Calendar;

public class CalendarCommon
{
    public static boolean equalOnlyDate(Calendar first_cal, Calendar second_cal)
    {
        if ((first_cal.getTime().getDate() == second_cal.getTime().getDate()) &&
                (first_cal.getTime().getMonth() == second_cal.getTime().getMonth()) &&
                (first_cal.getTime().getYear() == second_cal.getTime().getYear()))
            return true;
        else
            return false;
    }

    public static Calendar onlyDate(Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String convertDateElement(int dateElement)
    {
        String tempMonth = new String("");
        if (dateElement <= 9)
            return new String("0" + String.valueOf(dateElement));
        else if (dateElement > 9)
            return new String(String.valueOf(dateElement));
        return new String("");
    }
}
