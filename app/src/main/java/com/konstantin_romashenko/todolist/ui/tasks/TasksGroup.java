package com.konstantin_romashenko.todolist.ui.tasks;

import com.konstantin_romashenko.todolist.ui.common.TasksCommon;

import java.util.List;

public class TasksGroup
{

    public static TasksCommon.TaskType TaskType;
    private List<TaskItemClass> taskItems;
    private TasksCommon.TaskType groupType;

    private boolean expanded;

    TasksGroup(TasksCommon.TaskType taskType, List<TaskItemClass> taskItems)
    {
        this.groupType = taskType;
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

    public TasksCommon.TaskType getGroupType()
    {
        return groupType;
    }

    public void setGroupType(TasksCommon.TaskType groupType)
    {
        this.groupType = groupType;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}
