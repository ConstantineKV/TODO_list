package com.konstantin_romashenko.todolist.ui.tasks;

import java.util.Calendar;

public class TaskItemClass
{
    Integer id;
    Integer positionInList;
    String taskText;
    boolean status;
    Calendar dateAndTime;
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

    public Calendar getDateAndTime()
    {
        return dateAndTime;
    }
    public void setDateAndTime(Calendar dateAndTime)
    {
        this.dateAndTime = dateAndTime;
        dateSet = true;
    }
}
