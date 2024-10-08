package com.konstantin_romashenko.todolist.ui.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.konstantin_romashenko.todolist.ui.common.TasksCommon;
import com.konstantin_romashenko.todolist.ui.tasks.TasksFragment;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.konstantin_romashenko.todolist.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskClickListener;
public class TasksArrayAdapterExpandable extends BaseExpandableListAdapter
{
    private LayoutInflater inflater;
    private List<TaskItemClass> taskItems = new ArrayList<>();
    private Vector<TasksGroup> tasksByGroups;
    private TaskClickListener taskClickListener;

    public TasksArrayAdapterExpandable(@NonNull Context context, LayoutInflater inflater, TaskClickListener taskClickListener)
    {
        this.inflater = inflater;
        this.taskItems = taskItems;
        this.taskClickListener = taskClickListener;

    }

    void updateAdapter(Vector<TasksGroup> tasksByGroups)
    {
        this.tasksByGroups = tasksByGroups;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount()
    {
        return tasksByGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return tasksByGroups.get(groupPosition).getTaskItems().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {

        if (!tasksByGroups.get(groupPosition).getTaskItems().isEmpty())
        {
            GroupHolder groupHolder = new GroupHolder();
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.group_view_item, null, false);
                groupHolder.tvGroupName = convertView.findViewById(R.id.tvGroupName);
                convertView.setTag(groupHolder);

            }
            else
            {
                groupHolder = (GroupHolder)convertView.getTag();
            }

            groupHolder.tvGroupName.setText(tasksByGroups.get(groupPosition).getGroupType().toString());
            convertView.setVisibility(View.VISIBLE);
        }
        else
            convertView.setVisibility(View.INVISIBLE);
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        TaskHolder taskHolder;
        TaskItemClass taskItem;

        taskItem = tasksByGroups.get(groupPosition).getTaskItems().get(childPosition);

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_view_item, null ,false);
            taskHolder = new TaskHolder();
            taskHolder.layout = convertView.findViewById(R.id.clListViewItem);
            taskHolder.status = convertView.findViewById(R.id.cbStatus);
            taskHolder.configureListener();
            taskHolder.taskText = convertView.findViewById(R.id.tvTaskText);
            taskHolder.positionInList = convertView.findViewById(R.id.tvPositionInList);
            taskHolder.date = convertView.findViewById(R.id.tvDate);
            taskHolder.status.setTag(taskHolder);
            convertView.setTag(taskHolder);
        }
        else
        {
            taskHolder = (TaskHolder) convertView.getTag();
        }

        taskHolder.id = taskItem.id;
        taskHolder.status.setChecked(taskItem.status);
        taskHolder.taskText.setText(taskItem.taskText);
        if (tasksByGroups.get(groupPosition).getGroupType() == TasksCommon.TaskType.PREVIOUS)
        {
            taskHolder.positionInList.setTextColor(taskHolder.positionInList.getResources().getColor(R.color.taskText));
            taskHolder.taskText.setTextColor(taskHolder.taskText.getResources().getColor(R.color.taskText));
            taskHolder.date.setTextColor(taskHolder.date.getResources().getColor(R.color.taskTextExpired));
            taskHolder.taskText.setPaintFlags(taskHolder.taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else if (tasksByGroups.get(groupPosition).getGroupType() == TasksCommon.TaskType.TODAY)
        {
            taskHolder.positionInList.setTextColor(taskHolder.positionInList.getResources().getColor(R.color.taskText));
            taskHolder.taskText.setTextColor(taskHolder.taskText.getResources().getColor(R.color.taskText));
            taskHolder.date.setTextColor(taskHolder.date.getResources().getColor(R.color.taskText));
            taskHolder.taskText.setPaintFlags(taskHolder.taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else if (tasksByGroups.get(groupPosition).getGroupType() == TasksCommon.TaskType.FUTURE)
        {
            taskHolder.positionInList.setTextColor(taskHolder.positionInList.getResources().getColor(R.color.taskText));
            taskHolder.taskText.setTextColor(taskHolder.taskText.getResources().getColor(R.color.taskText));
            taskHolder.date.setTextColor(taskHolder.date.getResources().getColor(R.color.taskText));
            taskHolder.taskText.setPaintFlags(taskHolder.taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else if (tasksByGroups.get(groupPosition).getGroupType() == TasksCommon.TaskType.DONE)
        {
            taskHolder.positionInList.setTextColor(taskHolder.positionInList.getResources().getColor(R.color.taskTextDone));
            taskHolder.taskText.setTextColor(taskHolder.taskText.getResources().getColor(R.color.taskTextDone));
            taskHolder.date.setTextColor(taskHolder.date.getResources().getColor(R.color.taskTextDone));
            taskHolder.taskText.setPaintFlags(taskHolder.taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (tasksByGroups.get(groupPosition).getGroupType() != TasksCommon.TaskType.DONE)
        {
            taskHolder.positionInList.setVisibility(View.VISIBLE);
            taskHolder.positionInList.setText(String.format("%d.",taskItem.positionInList));
        }
        else
        {
            taskHolder.positionInList.setVisibility(View.INVISIBLE);
        }


        if ((taskHolder.date != null) && (taskItem.getDate() != null))
        {
            String simpleDate = new String(String.format("%s-%s",convertDateElement(taskItem.getDate().get(Calendar.MONTH) + 1),
                    convertDateElement(taskItem.getDate().get(Calendar.DAY_OF_MONTH))));
            taskHolder.date.setText(simpleDate);
        }
        else
            taskHolder.date.setText("");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

    boolean equalOnlyDate(Calendar first_cal, Calendar second_cal)
    {
        if ((first_cal.getTime().getDate() == second_cal.getTime().getDate()) &&
                (first_cal.getTime().getMonth() == second_cal.getTime().getMonth()) &&
                (first_cal.getTime().getYear() == second_cal.getTime().getYear()))
            return true;
        else
            return false;
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

    class GroupHolder
    {
        TextView tvGroupName;
    }

    private class TaskHolder implements View.OnClickListener
    {
        int id;
        CheckBox status;
        TextView taskText;
        TextView positionInList;
        TextView date;
        ConstraintLayout layout;

        TaskHolder()
        {

        }

        void configureListener()
        {
            if (status != null)
                status.setOnClickListener(this);
            if (layout != null)
                layout.setOnClickListener(this);
        }
        @Override
        public void onClick(View v)
        {
            if (v.getId() == R.id.cbStatus)
            {
                CheckBox radioButton = (CheckBox)v;
                if (radioButton.isChecked())
                {
                    try
                    {
                        taskClickListener.onTaskClicked(id,true);
                    }
                    catch (ParseException | InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try
                    {
                        taskClickListener.onTaskClicked(id,false);
                    }
                    catch (ParseException | InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            if (v.getId() == R.id.clListViewItem)
            {
                taskClickListener.onTaskLayoutClicked(id);
            }

        }
    }
}
