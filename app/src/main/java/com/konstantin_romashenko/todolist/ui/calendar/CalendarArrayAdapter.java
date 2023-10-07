package com.konstantin_romashenko.todolist.ui.calendar;

import static com.konstantin_romashenko.todolist.ui.common.CalendarCommon.convertDateElement;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konstantin_romashenko.todolist.R;
import com.konstantin_romashenko.todolist.ui.common.TasksCommon;
import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;
import com.konstantin_romashenko.todolist.ui.tasks.TasksArrayAdapter;
import com.konstantin_romashenko.todolist.ui.tasks.TasksGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

public class CalendarArrayAdapter extends RecyclerView.Adapter<CalendarArrayAdapter.TaskHolder>

{
    private List<TaskItemClass> taskItems;
    //private final LayoutInflater inflater;

    /*
    public CalendarArrayAdapter(@NonNull Context context, int resource, List<TaskItemClass> taskItems, LayoutInflater inflater)
    {
        super();
        this.inflater = inflater;
        this.taskItems = taskItems;
    }
    */
    public CalendarArrayAdapter( List<TaskItemClass> taskItems)
    {
        this.taskItems = taskItems;
    }
    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_item, parent, false);

        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position)
    {

        TaskItemClass taskItem = taskItems.get(position);
        holder.status.setVisibility(View.INVISIBLE);
        holder.taskText.setText(taskItem.getTaskText());
        holder.positionInList.setText(String.format("%d.",taskItem.getPositionInList()));

        if (taskItem.getDate() != null)
        {
            String simpleDate = new String(String.format("%s-%s",convertDateElement(taskItem.getDate().get(Calendar.MONTH) + 1),
                    convertDateElement(taskItem.getDate().get(Calendar.DAY_OF_MONTH))));
            holder.date.setText(simpleDate);
        }
        else
            holder.date.setText("");

        if (taskItem.getTaskType() == TasksCommon.TaskType.PREVIOUS)
        {
            holder.positionInList.setTextColor(holder.positionInList.getResources().getColor(R.color.taskText));
            holder.taskText.setTextColor(holder.taskText.getResources().getColor(R.color.taskText));
            holder.date.setTextColor(holder.date.getResources().getColor(R.color.taskTextExpired));
            holder.taskText.setPaintFlags(holder.taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else if (taskItem.getTaskType() == TasksCommon.TaskType.TODAY)
        {
            holder.positionInList.setTextColor(holder.positionInList.getResources().getColor(R.color.taskText));
            holder.taskText.setTextColor(holder.taskText.getResources().getColor(R.color.taskText));
            holder.date.setTextColor(holder.date.getResources().getColor(R.color.taskText));
            holder.taskText.setPaintFlags(holder.taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else if (taskItem.getTaskType() == TasksCommon.TaskType.FUTURE)
        {
            holder.positionInList.setTextColor(holder.positionInList.getResources().getColor(R.color.taskText));
            holder.taskText.setTextColor(holder.taskText.getResources().getColor(R.color.taskText));
            holder.date.setTextColor(holder.date.getResources().getColor(R.color.taskText));
            holder.taskText.setPaintFlags(holder.taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else if (taskItem.getTaskType() == TasksCommon.TaskType.DONE)
        {
            holder.positionInList.setTextColor(holder.positionInList.getResources().getColor(R.color.taskTextDone));
            holder.taskText.setTextColor(holder.taskText.getResources().getColor(R.color.taskTextDone));
            holder.date.setTextColor(holder.date.getResources().getColor(R.color.taskTextDone));
            holder.taskText.setPaintFlags(holder.taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (taskItem.getTaskType() != TasksCommon.TaskType.DONE)
        {
            holder.positionInList.setVisibility(View.VISIBLE);
            holder.positionInList.setText(String.format("%d.",taskItem.getPositionInList()));
        }
        else
        {
            holder.positionInList.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount()
    {
        return taskItems.size();
    }

    public void updateAdapter(ArrayList<TaskItemClass> tasks)
    {
        this.taskItems = tasks;
        notifyDataSetChanged();
    }
    public class TaskHolder extends RecyclerView.ViewHolder {
        CheckBox status;
        TextView taskText;
        TextView positionInList;
        TextView date;

        public TaskHolder(@NonNull View itemView)
        {
            super(itemView);

            if (itemView != null)
            {
                status = itemView.findViewById(R.id.cbStatus);
                taskText = itemView.findViewById(R.id.tvTaskText);
                positionInList = itemView.findViewById(R.id.tvPositionInList);
                date = itemView.findViewById(R.id.tvDate);
            }
        }
    }
}
