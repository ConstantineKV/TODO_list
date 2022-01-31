package com.konstantin_romashenko.todolist.ui.tasks;

import java.util.List;

public class TasksGroup
{
    private List<TaskItemClass> taskItems;
    private String              groupName;

    TasksGroup(String groupName, List<TaskItemClass> taskItems)
    {
        this.groupName = groupName;
        this.taskItems = taskItems;
    }
    public List<TaskItemClass> getTaskItems()
    {
        return taskItems;
    }

    public void setTaskItems(List<TaskItemClass> taskItems)
    {
        this.taskItems = taskItems;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
}
