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


    public static final String TABLE_GROUPS_NAME = "Groups";

    public static final String EXPANDED = "expanded";

    public static final String TABLE_GROUPS_CREATE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_GROUPS_NAME + " (" + _ID + " INTEGER PRIMARY KEY, " + EXPANDED + " BOOLEAN)";

    public static final String TABLE_GROUPS_GET_SIZE_STRUCTURE = "SELECT COUNT(*) FROM " +
            TABLE_GROUPS_NAME;

    public static final String DROP_TABLE_GROUPS = "DROP TABLE IF EXISTS " + TABLE_GROUPS_NAME;

    public static final int DB_VERSION = 4;
}
