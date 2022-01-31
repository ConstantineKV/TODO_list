package com.konstantin_romashenko.todolist.ui.tasks;

import java.text.ParseException;

public interface TaskClickListener
{
    void onTaskClicked(Integer id, boolean checkedStatus) throws ParseException;
}
