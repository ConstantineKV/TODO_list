package com.konstantin_romashenko.todolist.ui.tasks;

import android.app.Dialog;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.konstantin_romashenko.todolist.MainActivity;
import com.konstantin_romashenko.todolist.R;
import com.konstantin_romashenko.todolist.databinding.FragmentTasksBinding;
import com.konstantin_romashenko.todolist.ui.db.MyDBManager;
import com.konstantin_romashenko.todolist.ui.dialog.TaskAddDialog;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;

import kotlinx.coroutines.scheduling.Task;


public class TasksFragment extends Fragment implements View.OnClickListener, TaskTreatmentListener
{

    private TasksViewModel tasksViewModel;
    private TaskClickListener taskClickListener;
    private FragmentTasksBinding binding;
    private ExpandableListView tasksExpandableListView;
    ArrayList<TaskItemClass> previousTasks;
    ArrayList<TaskItemClass> todayTasks;
    ArrayList<TaskItemClass> futureTasks;
    ArrayList<TaskItemClass> doneTasks;
    private TasksArrayAdapterExpandable tasksAdapter;
    private MyDBManager myDB;
    private FloatingActionButton floatingActionButton;
    private Dialog dialog;
    private BottomSheetDialog bottomDialog;
    private TaskAddDialog taskAddDialog;


    private ArrayList<TaskItemClass> tasks;
    private Vector<TasksGroup> tasksByGroups = new Vector<TasksGroup>();


    enum TaskGroups
    {
        PREVIOUS(0),
        TODAY(1),
        FUTURE(2),
        DONE(3);

        private final int number;

        private TaskGroups(int number)
        {
            this.number = number;
        }

        public int getValue()
        {
            return number;
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);

        binding = FragmentTasksBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tasksExpandableListView = getView().findViewById(R.id.elvTasks);
        floatingActionButton = getView().findViewById(R.id.abCreateTask);
        floatingActionButton.setOnClickListener(this);

        setTaskClickListener();
        tasksAdapter = new TasksArrayAdapterExpandable(getActivity().getApplicationContext(), getLayoutInflater(), taskClickListener);

        myDB = new MyDBManager(getActivity().getApplicationContext());
        myDB.openDB();



        tasks = getDataFromDb();
        refreshData(tasks);

    }

    ArrayList<TaskItemClass> getDataFromDb()
    {
        ArrayList<TaskItemClass> tasksFromDb = new ArrayList<>();

        if (!myDB.isOpened())
            return tasksFromDb;

        try
        {
            tasksFromDb = myDB.getFromDB();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return tasksFromDb;
    }

    void refreshData(ArrayList<TaskItemClass> tasks)
    {
        if (tasksAdapter != null)
        {
            tasksToGroups(tasks, tasksByGroups);
            tasksAdapter.updateAdapter(tasksByGroups);
            tasksExpandableListView.setAdapter(tasksAdapter);
            int groupsCount = tasksByGroups.size();
            for (int i = 0; i < groupsCount; ++i)
            {
                tasksExpandableListView.expandGroup(i);
            }
        }

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
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        binding = null;
    }

    public void tasksToGroups(ArrayList<TaskItemClass> taskItems, Vector<TasksGroup> tasksByGroups)
    {
        Map<TaskGroups, List<TaskItemClass>> tasksByGroupsMap = new HashMap<>();

        if (taskItems.size() == 0)
            return;
        tasksByGroupsMap.clear();
        tasksByGroupsMap.put(TaskGroups.DONE, new ArrayList<>());
        tasksByGroupsMap.put(TaskGroups.FUTURE, new ArrayList<>());
        tasksByGroupsMap.put(TaskGroups.TODAY, new ArrayList<>());
        tasksByGroupsMap.put(TaskGroups.PREVIOUS, new ArrayList<>());

        for (TaskItemClass task : taskItems)
        {
            Calendar taskDate = task.getDate();

            Calendar currentDate = Calendar.getInstance();

            if (task.status)
                tasksByGroupsMap.get(TaskGroups.DONE).add(task);
            else if (task.getDate() == null)
                tasksByGroupsMap.get(TaskGroups.FUTURE).add(task);
            else if (equalOnlyDate(currentDate, task.getDate()))
                tasksByGroupsMap.get(TaskGroups.TODAY).add(task);
            else if (currentDate.getTime().after(task.getDate().getTime()))
                tasksByGroupsMap.get(TaskGroups.PREVIOUS).add(task);
            else if (currentDate.getTime().before(task.getDate().getTime()))
                tasksByGroupsMap.get(TaskGroups.FUTURE).add(task);
        }

        checkAndSortGroup(tasksByGroupsMap, TaskGroups.PREVIOUS);
        checkAndSortGroup(tasksByGroupsMap, TaskGroups.TODAY);
        checkAndSortGroup(tasksByGroupsMap, TaskGroups.FUTURE);
        checkAndSortGroup(tasksByGroupsMap, TaskGroups.DONE);

        tasksByGroups.clear();
        if (!tasksByGroupsMap.get(TaskGroups.PREVIOUS).isEmpty())
           tasksByGroups.add(new TasksGroup(TaskGroups.PREVIOUS.name(), tasksByGroupsMap.get(TaskGroups.PREVIOUS)));
        if (!tasksByGroupsMap.get(TaskGroups.TODAY).isEmpty())
            tasksByGroups.add(new TasksGroup(TaskGroups.TODAY.name(), tasksByGroupsMap.get(TaskGroups.TODAY)));
        if (!tasksByGroupsMap.get(TaskGroups.FUTURE).isEmpty())
            tasksByGroups.add(new TasksGroup(TaskGroups.FUTURE.name(), tasksByGroupsMap.get(TaskGroups.FUTURE)));
        if (!tasksByGroupsMap.get(TaskGroups.DONE).isEmpty())
            tasksByGroups.add(new TasksGroup(TaskGroups.DONE.name(), tasksByGroupsMap.get(TaskGroups.DONE)));

    }

    public void taskGroupsToVector(Map<TaskGroups, List<TaskItemClass>> tasksByGroups, Vector<TasksGroup> groupsVector)
    {

    }
    void checkAndSortGroup(Map<TaskGroups, List<TaskItemClass>> tasksByGroups, TaskGroups group)
    {
        if (!tasksByGroups.get(group).isEmpty())
            sortGroup(tasksByGroups.get(group));
    }

    void sortGroup(List<TaskItemClass> taskGroup)
    {
        taskGroup.sort(new Comparator<TaskItemClass>()
        {
            @Override
            public int compare(TaskItemClass o1, TaskItemClass o2)
            {
                return (o1.positionInList > o2.positionInList) ? 1 : -1;
            }
        });
    }

    TaskItemClass task;
    void setTaskClickListener()
    {
        taskClickListener = new TaskClickListener()
        {
            @Override
            public void onTaskClicked(Integer id, boolean checkedStatus) throws ParseException
            {
                for (TaskItemClass task : tasks)
                {
                    if (task.id == id)
                    {
                        task.status = checkedStatus;
                        myDB.updateInDB(id, task);
                    }
                }
                tasksToGroups(tasks, tasksByGroups);
                tasksAdapter.updateAdapter(tasksByGroups);
            }
        };
    }

    @Override
    public void onClick(View v)
    {
        taskAddDialog = new TaskAddDialog(getContext(), this);
        FragmentManager fm = getParentFragmentManager();

        if (tasks == null)
        {
            Log.e("TODO_Konstantin","TasksFragment.OnClick: no task in tasks array!");
            return;
        }

        taskAddDialog.setId(getFreeId());
        taskAddDialog.show(fm, "Add new task");
    }

    int getFreeId()
    {
        boolean idNotInList;
        Integer idToAdd = 0;
        do
        {
            ++idToAdd;
            idNotInList = true;
            for (TaskItemClass task : tasks)
            {
                if (idToAdd == task.getId())
                {
                    idNotInList = false;
                    break;
                }
            }
        }
        while (idNotInList != true);
        return idToAdd;
    }
    @Override
    public void onAddNewTaskClicked(TaskItemClass taskItem)
    {
        if (taskItem.getTaskText() == "")
            return;

        taskItem.id = getFreeId();

        myDB.insertToDB(taskItem);
        tasks = getDataFromDb();
        refreshData(tasks);

    }

    @Override
    public void onUpdateTaskClicked(TaskItemClass taskItem)
    {

    }

    @Override
    public void onDeleteTaskClicked(TaskItemClass taskItem)
    {

    }
}