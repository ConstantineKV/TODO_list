package com.konstantin_romashenko.todolist.ui.tasks.callbacks;

import java.text.ParseException;

public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onDragFinished() throws ParseException;
}
