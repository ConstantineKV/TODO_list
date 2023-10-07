package com.konstantin_romashenko.todolist.ui.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;

public class MyDBHelper extends SQLiteOpenHelper
{
    public MyDBHelper(@Nullable Context context)
    {
        super(context, MyConstants.TABLE_NAME, null, MyConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(MyConstants.TABLE_CREATE_STRUCTURE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(MyConstants.DROP_TABLE);
        onCreate(db);
    }


}
