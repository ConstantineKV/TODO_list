package com.konstantin_romashenko.todolist.ui.calendar;

import static com.konstantin_romashenko.todolist.ui.common.CalendarCommon.equalOnlyDate;
import static com.konstantin_romashenko.todolist.ui.common.TasksCommon.sortTasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konstantin_romashenko.todolist.R;
import com.konstantin_romashenko.todolist.databinding.FragmentCalendarBinding;

import com.konstantin_romashenko.todolist.ui.common.TasksCommon;
import com.konstantin_romashenko.todolist.ui.db.MyDBManager;
import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;

import org.jetbrains.annotations.NotNull;
import android.widget.CalendarView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CalendarFragment extends Fragment implements CalendarView.OnDateChangeListener
{
    private CalendarViewModel dashboardViewModel;
    private FragmentCalendarBinding binding;

    private CalendarView calendarView;
    private RecyclerView tasksRecyclerView;
    private ArrayList<TaskItemClass> tasks;
    private MyDBManager myDB;
    CalendarArrayAdapter arrayAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        dashboardViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        calendarView = getView().findViewById(R.id.cvTaskCalendar);
        calendarView.setOnDateChangeListener(this);
        tasksRecyclerView = getView().findViewById(R.id.rvTasksCalendar);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myDB = new MyDBManager(getActivity().getApplicationContext());
        myDB.openDB();
        Calendar calendar = Calendar.getInstance();

        try
        {
            tasks = getTasksByCalendar(calendar);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        sortTasks(tasks);

        arrayAdapter = new CalendarArrayAdapter(tasks);
        updateTasksType(tasks);
        arrayAdapter.updateAdapter(tasks);
        tasksRecyclerView.setAdapter(arrayAdapter);

    }

    void updateTasksType(List<TaskItemClass> tasks)
    {
        Calendar currentDate = Calendar.getInstance();
        for (TaskItemClass task : tasks)
        {
            if (task.getStatus())
                task.setTaskType(TasksCommon.TaskType.DONE);
            else if (task.getDate() == null)
                task.setTaskType(TasksCommon.TaskType.TODAY);
            else if (equalOnlyDate(currentDate, task.getDate()))
                task.setTaskType(TasksCommon.TaskType.TODAY);
            else if (currentDate.getTime().after(task.getDate().getTime()))
                task.setTaskType(TasksCommon.TaskType.PREVIOUS);
            else if (currentDate.getTime().before(task.getDate().getTime()))
                task.setTaskType(TasksCommon.TaskType.FUTURE);
        }
    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    ArrayList<TaskItemClass> getTasksByCalendar(Calendar calendar) throws ParseException {
        Calendar today = Calendar.getInstance();
        if (equalOnlyDate(calendar, today))
            return myDB.getFromDBbyCalendar(new Calendar[] {calendar, null});
        else
            return myDB.getFromDBbyCalendar(new Calendar[] {calendar});
    }
    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        try
        {
            tasks = getTasksByCalendar(calendar);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        sortTasks(tasks);
        updateTasksType(tasks);
        arrayAdapter.updateAdapter(tasks);
    }

}