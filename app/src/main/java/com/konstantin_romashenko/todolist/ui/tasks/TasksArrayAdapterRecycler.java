package com.konstantin_romashenko.todolist.ui.tasks;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.konstantin_romashenko.todolist.R;
import com.konstantin_romashenko.todolist.ui.common.TasksCommon;
import com.konstantin_romashenko.todolist.ui.tasks.callbacks.ItemTouchHelperAdapter;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskClickListener;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskDragListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import kotlinx.coroutines.scheduling.Task;

public class TasksArrayAdapterRecycler extends RecyclerView.Adapter<ViewHolder> implements ItemTouchHelperAdapter
{
    private static final int VIEW_TYPE_GROUP = 0;
    private static final int VIEW_TYPE_TASK = 1;

    private LayoutInflater inflater;
    private List<TaskItemClass> taskItems = new ArrayList<>();
    private Map<TasksCommon.TaskType, TasksGroup> tasksByGroups = new LinkedHashMap<>();
    private TaskClickListener taskClickListener;
    private TaskDragListener taskDragListener;

    public TasksArrayAdapterRecycler(@NonNull Context context, LayoutInflater inflater, TaskClickListener taskClickListener, TaskDragListener taskDragListener)
    {
        this.inflater = inflater;
        this.taskItems = taskItems;
        this.taskClickListener = taskClickListener;
        this.taskDragListener = taskDragListener;

    }

    void updateAdapter(Map<TasksCommon.TaskType, TasksGroup> tasksByGroups)
    {
        this.tasksByGroups = tasksByGroups;
        notifyDataSetChanged();
    }

    public Map<TasksCommon.TaskType, TasksGroup> getTasksByGroups()
    {
        return tasksByGroups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        if (viewType == VIEW_TYPE_GROUP)
        {
            itemView = inflater.inflate(R.layout.group_view_item, parent, false);
            return new GroupHolder(itemView);
        }
        else
        {
            itemView = inflater.inflate(R.layout.list_view_item, parent, false);
            return new TaskHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        int i = 0;

        if (holder instanceof GroupHolder)
        {
            ((GroupHolder) holder).bind((TasksGroup)getObjectByPosition(position));
        }
        else if (holder instanceof TaskHolder)
        {
            ((TaskHolder) holder).bind((TaskItemClass)getObjectByPosition(position));
        }
    }

    @Override
    public int getItemCount()
    {
        return getTaskItemsAbsoluteCount();
    }

    public int getTaskItemsAbsoluteCount()
    {
        int absoluteCount = 0;
        absoluteCount += tasksByGroups.size();
        for (TasksGroup tasksGroup : tasksByGroups.values())
        {
            absoluteCount += tasksGroup.getTaskItems().size();
        }
        return absoluteCount;
    }

    @Override
    public int getItemViewType(int position)
    {
        Object item = getObjectByPosition(position);
        if (item instanceof TasksGroup)
        {
            return VIEW_TYPE_GROUP;
        }
        else
        {
            return VIEW_TYPE_TASK;
        }
    }
    Object getObjectByPosition(int position)
    {
        int currentPosition = 0;
        for (TasksGroup tasksGroup : tasksByGroups.values())
        {
            if (position == currentPosition)
            {
                return tasksGroup;
            }
            else
            {
                for (TaskItemClass task : tasksGroup.getTaskItems())
                {
                    ++currentPosition;
                    if (position == currentPosition)
                        return task;
                }
            }
            ++currentPosition;
        }
        return null;
    }

    public void setExpanded(TasksCommon.TaskType groupType, boolean toExpand)
    {
        TasksGroup group = tasksByGroups.get(groupType);
        group.setExpanded(toExpand);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition)
    {
        Object fromObject = getObjectByPosition(fromPosition);
        Object toObject = getObjectByPosition(toPosition);
        if (toObject instanceof TaskItemClass)
        {
            TaskItemClass fromTask = (TaskItemClass) fromObject;
            TaskItemClass toTask = (TaskItemClass) toObject;
            if (toTask.getTaskType() == fromTask.getTaskType())
            {
                int fromTaskIndex = tasksByGroups.get(toTask.getTaskType()).getTaskItems().indexOf(fromTask);
                int toTaskIndex = tasksByGroups.get(toTask.getTaskType()).getTaskItems().indexOf(toTask);
                Collections.swap(tasksByGroups.get(toTask.getTaskType()).getTaskItems(), fromTaskIndex, toTaskIndex);
                if (toTask.getTaskType() != TasksCommon.TaskType.DONE)
                    fromTask.setDate(toTask.getDate());
                notifyItemMoved(fromPosition, toPosition);
            }
        }

    }

    @Override
    public void onDragFinished() throws ParseException {
        taskDragListener.onDragFinished();
    }

    public class GroupHolder extends ViewHolder implements View.OnClickListener
    {
        TextView tvGroupName;
        ConstraintLayout layout;
        TasksGroup tasksGroup;
        ImageView arrow;
        LinearLayout linearLayout;
        View itemView;
        AnimationDrawable clickAnimation;
        Animation animation;
        public GroupHolder(@NonNull View itemView)
        {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            layout = itemView.findViewById(R.id.gvConstraintLayout);
            linearLayout = itemView.findViewById(R.id.gvBackground);
            arrow = itemView.findViewById(R.id.gvArrow);
            layout.setOnClickListener(this);
            tvGroupName.setOnClickListener(this);
            linearLayout.setOnClickListener(this);
            this.itemView = itemView;

            animation = new AlphaAnimation(0.0f, 0.05f);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(100);
            animation.setRepeatCount(1);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation)
                {

                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    linearLayout.setAlpha(0.0f);
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });
        }

        public void bind(TasksGroup tasksGroup)
        {
            this.tasksGroup = tasksGroup;

            tvGroupName.setText(tasksGroup.getGroupType().toString());
            if (tasksGroup.isExpanded())
                arrow.setImageResource(R.drawable.recycler_view_arrow_close_list);
            else
                arrow.setImageResource(R.drawable.recycler_view_arrow_open_list);
        }

        @Override
        public void onClick(View v)
        {
            tasksGroup.setExpanded(!tasksGroup.isExpanded());
            if (tasksGroup.isExpanded())
                arrow.setImageResource(R.drawable.recycler_view_arrow_close_list);
            else
                arrow.setImageResource(R.drawable.recycler_view_arrow_open_list);

            linearLayout.setAlpha(1.0f);
            linearLayout.startAnimation(animation);
            refreshTasksByType(tasksGroup.getGroupType());
        }

        public void refreshTasksByType(TasksCommon.TaskType type)
        {
            for (int i = 0; i < getTaskItemsAbsoluteCount(); ++i)
            {
                Object object = getObjectByPosition(i);
                if (object instanceof TasksGroup)
                {
                    continue;
                }
                else
                {
                    TaskItemClass taskItem = (TaskItemClass)object;
                    if (taskItem.getTaskType() == type)
                        notifyItemChanged(i);
                }
            }
        }
    }

    public class TaskHolder extends ViewHolder implements View.OnClickListener {
        int id;
        CheckBox status;
        TextView taskText;
        TextView positionInList;
        TextView date;
        ConstraintLayout layout;

        TaskHolder(@NonNull View itemView)
        {
            super(itemView);

            layout = itemView.findViewById(R.id.clListViewItem);
            status = itemView.findViewById(R.id.cbStatus);
            taskText = itemView.findViewById(R.id.tvTaskText);
            positionInList = itemView.findViewById(R.id.tvPositionInList);
            date = itemView.findViewById(R.id.tvDate);
            configureListener();
        }

        public void bind(TaskItemClass taskItem)
        {
            if (tasksByGroups.get(taskItem.getTaskType()).isExpanded())
            {
                layout.setVisibility(View.VISIBLE);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0,0,10);
                layout.setLayoutParams(layoutParams);
            }
            else
            {
                layout.setVisibility(View.GONE);
                layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            id = taskItem.id;
            status.setChecked(taskItem.status);
            taskText.setText(taskItem.taskText);

            if (taskItem.getTaskType() == TasksCommon.TaskType.PREVIOUS)
            {
                positionInList.setTextColor(positionInList.getResources().getColor(R.color.taskText));
                taskText.setTextColor(taskText.getResources().getColor(R.color.taskText));
                date.setTextColor(date.getResources().getColor(R.color.taskTextExpired));
                taskText.setPaintFlags(taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

            }
            else if (taskItem.getTaskType() == TasksCommon.TaskType.TODAY)
            {
                positionInList.setTextColor(positionInList.getResources().getColor(R.color.taskText));
                taskText.setTextColor(taskText.getResources().getColor(R.color.taskText));
                date.setTextColor(date.getResources().getColor(R.color.taskText));
                taskText.setPaintFlags(taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else if (taskItem.getTaskType() == TasksCommon.TaskType.FUTURE)
            {
                positionInList.setTextColor(positionInList.getResources().getColor(R.color.taskText));
                taskText.setTextColor(taskText.getResources().getColor(R.color.taskText));
                date.setTextColor(date.getResources().getColor(R.color.taskText));
                taskText.setPaintFlags(taskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else if (taskItem.getTaskType() == TasksCommon.TaskType.DONE)
            {
                positionInList.setTextColor(positionInList.getResources().getColor(R.color.taskTextDone));
                taskText.setTextColor(taskText.getResources().getColor(R.color.taskTextDone));
                date.setTextColor(date.getResources().getColor(R.color.taskTextDone));
                taskText.setPaintFlags(taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            if (taskItem.getTaskType() != TasksCommon.TaskType.DONE)
            {
                positionInList.setVisibility(View.VISIBLE);
                positionInList.setText(String.format("%d.",taskItem.positionInList));
            }
            else
            {
                positionInList.setVisibility(View.INVISIBLE);
            }

            if ((date != null) && (taskItem.getDate() != null))
            {
                String simpleDate = new String(String.format("%s-%s",convertDateElement(taskItem.getDate().get(Calendar.MONTH) + 1),
                        convertDateElement(taskItem.getDate().get(Calendar.DAY_OF_MONTH))));
                date.setText(simpleDate);
            }
            else
                date.setText("");
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

        void configureListener() {
            if (status != null)
                status.setOnClickListener(this);
            if (layout != null)
                layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cbStatus) {
                CheckBox radioButton = (CheckBox) v;
                if (radioButton.isChecked()) {
                    try {
                        taskClickListener.onTaskClicked(id, true);
                    } catch (ParseException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        taskClickListener.onTaskClicked(id, false);
                    } catch (ParseException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (v.getId() == R.id.clListViewItem) {
                taskClickListener.onTaskLayoutClicked(id);
            }

        }

    }

}