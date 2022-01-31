package com.konstantin_romashenko.todolist.ui.tasks;

public interface TaskTreatmentListener
{
    void onAddNewTaskClicked(TaskItemClass taskItem);
    void onUpdateTaskClicked(TaskItemClass taskItem);
    void onDeleteTaskClicked(TaskItemClass taskItem);
}
