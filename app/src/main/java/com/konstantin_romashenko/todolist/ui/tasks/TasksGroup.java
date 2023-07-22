package com.konstantin_romashenko.todolist.ui.tasks;

import java.util.List;

public class TasksGroup
{
    enum TaskType
    {
        PREVIOUS(0),
        TODAY(1),
        FUTURE(2),
        DONE(3);

        private final int number;

        private TaskType(int number)
        {
            this.number = number;
        }

        public int getValue()
        {
            return number;
        }
    }
    private List<TaskItemClass> taskItems;
    private TaskType groupType;

    TasksGroup(TaskType taskType, List<TaskItemClass> taskItems)
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

    public TaskType getGroupType()
    {
        return groupType;
    }

    public void setGroupType(TaskType groupType)
    {
        this.groupType = groupType;
    }

}
