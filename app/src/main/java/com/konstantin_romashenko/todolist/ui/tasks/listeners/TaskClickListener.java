package com.konstantin_romashenko.todolist.ui.tasks.listeners;

import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;

import java.text.ParseException;

public interface TaskClickListener
{
    void onTaskClicked(Integer id, boolean checkedStatus) throws ParseException, InterruptedException;
    void onTaskLayoutClicked(Integer id);
}
