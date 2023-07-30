package com.konstantin_romashenko.todolist.ui.common;

import static com.konstantin_romashenko.todolist.ui.common.CalendarCommon.equalOnlyDate;

import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;
import com.konstantin_romashenko.todolist.ui.tasks.TasksGroup;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TasksCommon
{
    public enum TaskType
    {
        PREVIOUS,
        TODAY,
        FUTURE,
        DONE
    }
    public static void checkAndSortTasks(Map<TaskType, List<TaskItemClass>> tasksByGroups, TaskType taskType)
    {
        if (!tasksByGroups.get(taskType).isEmpty())
            sortTasks(tasksByGroups.get(taskType));
    }

    public static void sortTasks(List<TaskItemClass> taskGroup)
    {
        taskGroup.sort(new Comparator<TaskItemClass>()
        {
            @Override
            public int compare(TaskItemClass o1, TaskItemClass o2)
            {
                Calendar o1date = o1.getDate() != null? o1.getDate() : Calendar.getInstance();
                Calendar o2date = o2.getDate() != null? o2.getDate() : Calendar.getInstance();
                if (equalOnlyDate(o1date, o2date))
                {
                    return (o1.getPositionInList() > o2.getPositionInList()) ? 1 : -1;
                }
                if (o1date.after(o2date))
                    return 1;
                else
                    return -1;
            }
        });
    }
}
