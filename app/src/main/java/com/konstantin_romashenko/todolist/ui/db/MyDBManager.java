package com.konstantin_romashenko.todolist.ui.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyDBManager
{
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
        if (taskItem.getDateAndTime() != null)
        {
            String dateString = new String();
            dateString = fromCalendarToString(taskItem.getDateAndTime());
            cv.put(MyConstants.DATE_AND_TIME, dateString);
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

        if (taskItem.getDateAndTime() != null)
        {
            String dateString = new String();
            dateString = fromCalendarToString(taskItem.getDateAndTime());
            cv.put(MyConstants.DATE_AND_TIME, dateString);
        }

        db.update(MyConstants.TABLE_NAME, cv, "_id = ?", new String[] {Integer.toString(id)});
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

            Calendar calendar = Calendar.getInstance();
            calendar = fromStringToCalendar(cursor.getString(cursor.getColumnIndex(MyConstants.DATE_AND_TIME)));
            tempTaskItem.setDateAndTime(calendar);
            tasks.add(tempTaskItem);
        }
        cursor.close();
        return tasks;
    }

    Calendar fromStringToCalendar(String strDate) throws ParseException
    {
        Calendar calendar;

        if (strDate != null)
        {
            calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            calendar.setTime(sdf.parse(strDate));
            return calendar;
        }
        else
        {
            return null;
        }
    }

    String fromCalendarToString(Calendar calendarDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(calendarDate.getTime());
    }
    void closeDB()
    {
        myDBHelper.close();
    }
}
