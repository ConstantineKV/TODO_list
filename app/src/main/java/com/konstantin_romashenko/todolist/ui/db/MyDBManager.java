package com.konstantin_romashenko.todolist.ui.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyDBManager
{

    //private static MyDBManager myDBManager;
    private Context context;
    private MyDBHelper myDBHelper;
    private SQLiteDatabase db;

    public MyDBManager(Context context)
    {
        this.context = context;
        myDBHelper = new MyDBHelper(context);

    }

    public void openDB()
    {
        db = myDBHelper.getWritableDatabase();
    }

    public boolean isOpened()
    {
        return db.isOpen();
    }

    public void insertToDB(TaskItemClass taskItem)
    {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants._ID, taskItem.getId());
        cv.put(MyConstants.POSITION_IN_LIST, taskItem.getPositionInList());
        cv.put(MyConstants.TASK_TEXT, taskItem.getTaskText());
        if (taskItem.getDate() != null)
        {
            String dateString;
            dateString = fromCalendarToDateString(taskItem.getDate());
            cv.put(MyConstants.DATE, dateString);
        }
        if (taskItem.getTime() != null)
        {
            String timeString;
            timeString = fromCalendarToTimeString(taskItem.getTime());
            cv.put(MyConstants.TIME, timeString);
        }

        cv.put(MyConstants.STATUS, taskItem.getStatus() ? 1 : 0);
        db.insert(MyConstants.TABLE_NAME, null, cv);


    }

    public void updateInDB(Integer id, TaskItemClass taskItem) throws ParseException
    {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.TASK_TEXT, taskItem.getTaskText());
        cv.put(MyConstants.STATUS, taskItem.getStatus() ? 1 : 0);
        cv.put(MyConstants.POSITION_IN_LIST, taskItem.getPositionInList());

        if (taskItem.getDate() != null)
        {
            String dateString = new String();
            dateString = fromCalendarToDateString(taskItem.getDate());
            cv.put(MyConstants.DATE, dateString);

        }
        if (taskItem.getTime() != null)
        {
            String timeString = new String();
            timeString = fromCalendarToTimeString(taskItem.getTime());
            cv.put(MyConstants.TIME, timeString);
        }

        db.update(MyConstants.TABLE_NAME, cv, "_id = ?", new String[] {Integer.toString(id)});
    }

    public void updateAllInDb(ArrayList<TaskItemClass> tasks) throws ParseException
    {
        for (TaskItemClass task : tasks)
        {
            updateInDB(task.getId(), task);
        }
    }
    @SuppressLint("Range")
    public ArrayList<TaskItemClass> getFromDB() throws ParseException
    {
        ArrayList<TaskItemClass> tasks = new ArrayList<>();
        Cursor cursor = db.query(MyConstants.TABLE_NAME, null, null, null,
                null, null, null);
        while (cursor.moveToNext())
        {
            TaskItemClass tempTaskItem = new TaskItemClass();
            tempTaskItem.setId(cursor.getInt(cursor.getColumnIndex(MyConstants._ID)));
            tempTaskItem.setTaskText(cursor.getString(cursor.getColumnIndex(MyConstants.TASK_TEXT)));
            boolean tempStatus = cursor.getInt(cursor.getColumnIndex(MyConstants.STATUS)) > 0 ? true : false;
            tempTaskItem.setStatus(tempStatus);
            tempTaskItem.setPositionInList(cursor.getInt(cursor.getColumnIndex(MyConstants.POSITION_IN_LIST)));

            Calendar calendarDate;
            calendarDate = fromDateStringToCalendar(cursor.getString(cursor.getColumnIndex(MyConstants.DATE)));
            tempTaskItem.setDate(calendarDate);
            Calendar calendarTime;
            calendarTime = fromTimeStringToCalendar(cursor.getString(cursor.getColumnIndex(MyConstants.TIME)));
            tempTaskItem.setTime(calendarTime);

            tasks.add(tempTaskItem);
        }
        cursor.close();
        return tasks;
    }


    @SuppressLint("Range")
    public ArrayList<TaskItemClass> getFromDBbyCalendar(Calendar[] calendars) throws ParseException
    {
        ArrayList<String> dateStrings = new ArrayList<String>();
        String selection = "date = ?";
        for (int i = 0; i < calendars.length; ++i)
        {
            if (calendars[i] == null)
                selection = "date IS NULL OR date = ?";
            else
                dateStrings.add(fromCalendarToDateString(calendars[i]));
        }

        ArrayList<TaskItemClass> tasks = new ArrayList<>();
        Cursor cursor = db.query(MyConstants.TABLE_NAME, (String[]) null, selection, dateStrings.toArray(new String[0]),
                null, null, null);
        while (cursor.moveToNext())
        {
            TaskItemClass tempTaskItem = new TaskItemClass();
            tempTaskItem.setId(cursor.getInt(cursor.getColumnIndex(MyConstants._ID)));
            tempTaskItem.setTaskText(cursor.getString(cursor.getColumnIndex(MyConstants.TASK_TEXT)));
            boolean tempStatus = cursor.getInt(cursor.getColumnIndex(MyConstants.STATUS)) > 0 ? true : false;
            tempTaskItem.setStatus(tempStatus);
            tempTaskItem.setPositionInList(cursor.getInt(cursor.getColumnIndex(MyConstants.POSITION_IN_LIST)));

            Calendar calendarDate;
            calendarDate = fromDateStringToCalendar(cursor.getString(cursor.getColumnIndex(MyConstants.DATE)));
            tempTaskItem.setDate(calendarDate);
            Calendar calendarTime;
            calendarTime = fromTimeStringToCalendar(cursor.getString(cursor.getColumnIndex(MyConstants.TIME)));
            tempTaskItem.setTime(calendarTime);

            tasks.add(tempTaskItem);
        }
        cursor.close();
        return tasks;
    }

    public void deleteInDb(Integer id)
    {
        db.delete(MyConstants.TABLE_NAME, "_id = ?", new String[] {Integer.toString(id)});
    }

    Calendar fromDateStringToCalendar(String strDate) throws ParseException
    {
        Calendar calendar;

        if (strDate != null)
        {
            calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(sdf.parse(strDate));
            return calendar;
        }
        else
        {
            return null;
        }
    }

    Calendar fromTimeStringToCalendar(String strDate) throws ParseException
    {
        Calendar calendar;

        if (strDate != null)
        {
            calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            calendar.setTime(sdf.parse(strDate));
            return calendar;
        }
        else
        {
            return null;
        }
    }

    String fromCalendarToDateString(Calendar calendarDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendarDate.getTime());
    }

    String fromCalendarToTimeString(Calendar calendarDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(calendarDate.getTime());
    }


    public void closeDB()
    {
        myDBHelper.close();
    }


    /*
    public static MyDBManager getInstante()
    {
        if (myDBManager == null)
        {
            myDBManager = new MyDBManager(this.context);
        }
    }*/


}
