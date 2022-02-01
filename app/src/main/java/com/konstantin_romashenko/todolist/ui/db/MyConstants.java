package com.konstantin_romashenko.todolist.ui.db;

public class MyConstants
{
    public static final String TABLE_NAME = "Tasks";
    public static final String _ID = "_id";
    public static final String TASK_TEXT = "text";
    public static final String STATUS = "status";
    public static final String POSITION_IN_LIST = "position_in_list";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String DATE_IS_SET = "date_is_set";
    public static final String TABLE_CREATE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY, " + POSITION_IN_LIST + " TEXT, " + TASK_TEXT + " TEXT," + STATUS + " INTEGER, " +
            DATE + " TEXT, " + TIME + " TEXT, " + DATE_IS_SET + " INTEGER)";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final int DB_VERSION = 3;

}
