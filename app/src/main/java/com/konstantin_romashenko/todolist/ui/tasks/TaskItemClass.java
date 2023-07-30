package com.konstantin_romashenko.todolist.ui.tasks;

import androidx.recyclerview.widget.RecyclerView;

import com.konstantin_romashenko.todolist.ui.common.TasksCommon;

import java.io.Serializable;
import java.util.Calendar;

public class TaskItemClass implements Serializable
{
    Integer id;
    Integer positionInList;
    String taskText;
    boolean status;
    Calendar date;



    TasksCommon.TaskType taskType = TasksCommon.TaskType.TODAY;

    Calendar time;
    boolean dateSet = false;

    public TaskItemClass()
    {
        id = 0;
        positionInList = 0;
        taskText = new String();
        status = false;
    }

    public boolean isStatus()
    {
        return status;
    }
    public boolean isDateSet()
    {
        return dateSet;
    }
    public void setDateSet(boolean dateSet)
    {
        this.dateSet = dateSet;
    }

    public Integer getId() { return id; };
    public void setId(Integer id) { this.id = id; }

    public boolean getStatus()
    {
        return status;
    }
    public void setStatus(boolean done)
    {
        this.status = done;
    }

    public String getTaskText()
    {
        return taskText;
    }
    public void setTaskText(String taskText)
    {
        this.taskText = taskText;
    }

    public Integer getPositionInList()
    {
        return positionInList;
    }
    public void setPositionInList(Integer positionInList)
    {
        this.positionInList = positionInList;
    }

    public Calendar getDate()
    {
        return date;
    }
    public void setDate(Calendar date)
    {
        this.date = date;
        dateSet = true;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public TasksCommon.TaskType getTaskType()
    {
        return taskType;
    }

    public void setTaskType(TasksCommon.TaskType taskType)
    {
        this.taskType = taskType;
    }
}
