package com.konstantin_romashenko.todolist.ui.tasks.listeners;

import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;

import java.io.Serializable;
import java.text.ParseException;

public abstract class TaskTreatmentListener implements Serializable
{
    public abstract void onUpdateTaskClicked(TaskItemClass taskItem) throws ParseException;
    public abstract void onDeleteTaskClicked(TaskItemClass taskItem);
}
