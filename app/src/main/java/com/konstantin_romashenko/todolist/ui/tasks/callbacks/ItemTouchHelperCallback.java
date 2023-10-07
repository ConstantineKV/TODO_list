package com.konstantin_romashenko.todolist.ui.tasks.callbacks;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.konstantin_romashenko.todolist.ui.tasks.TasksArrayAdapterRecycler;

import java.text.ParseException;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback
{
    private final ItemTouchHelperAdapter adapter;

    public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter)
    {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
        int swipeFlags = 0;
        if (viewHolder instanceof TasksArrayAdapterRecycler.GroupHolder)
        {
            dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
            swipeFlags = 0;
        }
        else if (viewHolder instanceof TasksArrayAdapterRecycler.TaskHolder)
        {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeFlags = 0;
        }

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target)
    {
        adapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {

    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof TasksArrayAdapterRecycler.TaskHolder)
        {
            TasksArrayAdapterRecycler.TaskHolder taskHolder = (TasksArrayAdapterRecycler.TaskHolder) viewHolder;

        }
        try
        {
            adapter.onDragFinished();
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }
}