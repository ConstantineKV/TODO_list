package com.konstantin_romashenko.todolist.ui.tasks.listeners;

import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;

import java.text.ParseException;

public interface TaskAddListener
{
    void onAddNewTaskClicked(TaskItemClass taskItem) throws ParseException;

}
