package com.konstantin_romashenko.todolist.ui.tasks.listeners;

import android.view.DragEvent;
import android.view.View;
import android.widget.ExpandableListView;

import java.text.ParseException;

public interface TaskDragListener
{
    public void onDragFinished() throws ParseException;
}
