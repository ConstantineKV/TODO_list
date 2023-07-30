package com.konstantin_romashenko.todolist.ui.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.konstantin_romashenko.todolist.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TasksArrayAdapter extends ArrayAdapter<TaskItemClass>
{
    private LayoutInflater inflater;
    private List<TaskItemClass> taskItems = new ArrayList<>();

    public TasksArrayAdapter(@NonNull Context context, int resource, List<TaskItemClass> taskItems, LayoutInflater inflater)
    {
        super(context, resource, taskItems);
        this.inflater = inflater;
        this.taskItems = taskItems;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TaskHolder taskHolder;
        TaskItemClass taskItem = taskItems.get(position);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_view_item, null ,false);
            taskHolder = new TaskHolder();
            taskHolder.status = convertView.findViewById(R.id.cbStatus);
            taskHolder.taskText = convertView.findViewById(R.id.tvTaskText);
            taskHolder.positionInList = convertView.findViewById(R.id.tvPositionInList);
            taskHolder.date = convertView.findViewById(R.id.tvDate);

            convertView.setTag(taskHolder);
        }
        else
        {
            taskHolder = (TaskHolder) convertView.getTag();
        }

        taskHolder.status.setChecked(taskItem.status);
        taskHolder.taskText.setText(taskItem.taskText);
        taskHolder.positionInList.setText(String.format("%d.",taskItem.positionInList));
        int test = taskItem.getDate().get(Calendar.MONTH);

        if (taskHolder.date != null)
        {
            String simpleDate = new String(String.format("%s-%s",convertDateElement(taskItem.getDate().get(Calendar.MONTH) + 1),
                convertDateElement(taskItem.getDate().get(Calendar.DAY_OF_MONTH))));
            taskHolder.date.setText(simpleDate);
        }
        else
            taskHolder.date.setText("");

        return convertView;
    }

    String convertDateElement(int dateElement)
    {
        String tempMonth = new String("");
        if (dateElement < 1)
            return new String("");
        else if (dateElement <= 9)
            return new String("0" + String.valueOf(dateElement));
        else if (dateElement > 9)
            return new String(String.valueOf(dateElement));
        return new String("");
    }
    public class TaskHolder
    {
        RadioButton status;
        TextView taskText;
        TextView positionInList;
        TextView date;
    }
}
