package com.konstantin_romashenko.todolist.ui.tasks.listeners;

import androidx.fragment.app.Fragment;

import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;
import com.konstantin_romashenko.todolist.ui.tasks.TasksFragment;

import java.text.ParseException;

public class TaskTreatmentListenerImpl extends TaskTreatmentListener
{
    TasksFragment tasksFragment;
    public TaskTreatmentListenerImpl(Fragment tasksFragment)
    {
        //this.tasksFragment = tasksFragment;
    }
    @Override
    public void onUpdateTaskClicked(TaskItemClass taskItem) throws ParseException
    {
        tasksFragment.onUpdateTaskClicked(taskItem);
    }

    @Override
    public void onDeleteTaskClicked(TaskItemClass taskItem)
    {
        tasksFragment.onDeleteTaskClicked(taskItem);
    }
}

