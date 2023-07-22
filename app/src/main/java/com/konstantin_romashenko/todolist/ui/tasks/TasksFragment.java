package com.konstantin_romashenko.todolist.ui.tasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.konstantin_romashenko.todolist.R;
import com.konstantin_romashenko.todolist.databinding.FragmentTasksBinding;
import com.konstantin_romashenko.todolist.ui.db.MyDBManager;
import com.konstantin_romashenko.todolist.ui.dialog.TaskAddDialog;

import org.jetbrains.annotations.NotNull;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskClickListener;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskAddListener;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskTreatmentListener;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskTreatmentListenerImpl;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class TasksFragment extends Fragment implements View.OnClickListener, TaskClickListener, TaskAddListener, Serializable
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
    private TaskTreatmentListenerImpl taskTreatmentListener;
    public boolean[] expanded;
    class GroupExpandedInfo
    {
        public boolean expanded = false;
        public TasksGroup.TaskType taskType = TasksGroup.TaskType.PREVIOUS;
    };
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == 1)
                    {
                        Intent intent = result.getData();
                        TaskItemClass taskItem = (TaskItemClass)intent.getSerializableExtra("taskItem");
                        if (taskItem.getTaskText() == "")
                            return;

                        try
                        {
                            myDB.updateInDB(taskItem.getId(), taskItem);
                        }
                        catch (ParseException e)
                        {
                            throw new RuntimeException(e);
                        }
                        refreshDataFromDb();
                    }
                    if (result.getResultCode() == 2)
                    {
                        Intent intent = result.getData();
                        TaskItemClass taskItem = (TaskItemClass)intent.getSerializableExtra("taskItem");

                        myDB.deleteInDb(taskItem.getId());
                        refreshDataFromDb();
                    }
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);

        binding = FragmentTasksBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        taskTreatmentListener = new TaskTreatmentListenerImpl(this);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tasksExpandableListView = getView().findViewById(R.id.elvTasks);
        floatingActionButton = getView().findViewById(R.id.abCreateTask);
        floatingActionButton.setOnClickListener(this);

        tasksAdapter = new TasksArrayAdapterExpandable(getActivity().getApplicationContext(), getLayoutInflater(), this);

        myDB = new MyDBManager(getActivity().getApplicationContext());
        myDB.openDB();
        refreshDataFromDb();
        int todayGroupIndex = -1;
        for (int i = 0; i < tasksByGroups.size(); ++i)
        {
            TasksGroup tasksGroup = tasksByGroups.get(i);
            if (tasksGroup.getGroupType() == TasksGroup.TaskType.TODAY)
                todayGroupIndex = i;
        }
        if (todayGroupIndex != -1)
            tasksExpandableListView.expandGroup(todayGroupIndex);
    }

    void refreshDataFromDb()
    {
        tasks = getDataFromDb();
        correctPositionInList(tasks);
        updateDataInDb();
        refreshExpandableView(tasks);
    }
    void correctPositionInList(ArrayList<TaskItemClass> tasks)
    {
        sortTasks(tasks);

        TaskItemClass previousTask = null;
        for (TaskItemClass task : tasks)
        {
            if (task.status == true)
            {
                task.positionInList = 99999;
            }
            else
            {
                if (previousTask == null)
                    task.setPositionInList(1);
                else
                {
                    Calendar previousTaskDate = previousTask.getDate() != null? previousTask.getDate() : Calendar.getInstance();
                    Calendar currentTaskDate = task.getDate() != null? task.getDate() : Calendar.getInstance();
                    if (!equalOnlyDate(previousTaskDate, currentTaskDate))
                        task.setPositionInList(1);
                    else
                        task.setPositionInList(previousTask.getPositionInList() + 1);
                }
                previousTask = task;
            }
        }
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

    void updateDataInDb()
    {
        if (!myDB.isOpened())
            return;

        try
        {
            myDB.updateAllInDb(tasks);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }
    void refreshExpandableView(ArrayList<TaskItemClass> tasks)
    {
        if (tasksAdapter != null)
        {
            GroupExpandedInfo[] expanded = getExpanded(tasksByGroups.size());
            tasksToGroups(tasks, tasksByGroups);
            tasksAdapter.updateAdapter(tasksByGroups);
            tasksExpandableListView.setAdapter(tasksAdapter);
            setExpanded(expanded);
        }

    }

    GroupExpandedInfo[] getExpanded(int groupsCount)
    {
        GroupExpandedInfo[] expanded = new GroupExpandedInfo[groupsCount];
        for (int i = 0; i < groupsCount; ++i)
        {
            expanded[i] = new GroupExpandedInfo();
            expanded[i].expanded = tasksExpandableListView.isGroupExpanded(i);
            expanded[i].taskType = tasksByGroups.get(i).getGroupType();
        }
        return expanded;
    }

    void setExpanded(GroupExpandedInfo[] expanded)
    {
        for (int i = 0; i < tasksByGroups.size(); ++i)
        {
            if (isGroupShouldBeExpanded(tasksByGroups.get(i), expanded))
                tasksExpandableListView.expandGroup(i);
        }
    }

    boolean isGroupShouldBeExpanded(TasksGroup tasksGroup, GroupExpandedInfo[] expanded)
    {
        for (GroupExpandedInfo info : expanded)
        {
            if (tasksGroup.getGroupType() == info.taskType)
            {
                return info.expanded;
            }
        }
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
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        binding = null;
    }

    public void tasksToGroups(ArrayList<TaskItemClass> taskItems, Vector<TasksGroup> tasksByGroups)
    {
        Map<TasksGroup.TaskType, List<TaskItemClass>> tasksByGroupsMap = new HashMap<>();

        tasksByGroupsMap.clear();
        tasksByGroupsMap.put(TasksGroup.TaskType.DONE, new ArrayList<>());
        tasksByGroupsMap.put(TasksGroup.TaskType.FUTURE, new ArrayList<>());
        tasksByGroupsMap.put(TasksGroup.TaskType.TODAY, new ArrayList<>());
        tasksByGroupsMap.put(TasksGroup.TaskType.PREVIOUS, new ArrayList<>());

        for (TaskItemClass task : taskItems)
        {
            Calendar taskDate = task.getDate();

            Calendar currentDate = Calendar.getInstance();

            if (task.status)
                tasksByGroupsMap.get(TasksGroup.TaskType.DONE).add(task);
            else if (task.getDate() == null)
                tasksByGroupsMap.get(TasksGroup.TaskType.TODAY).add(task);
            else if (equalOnlyDate(currentDate, task.getDate()))
                tasksByGroupsMap.get(TasksGroup.TaskType.TODAY).add(task);
            else if (currentDate.getTime().after(task.getDate().getTime()))
                tasksByGroupsMap.get(TasksGroup.TaskType.PREVIOUS).add(task);
            else if (currentDate.getTime().before(task.getDate().getTime()))
                tasksByGroupsMap.get(TasksGroup.TaskType.FUTURE).add(task);
        }

        checkAndSortTasks(tasksByGroupsMap, TasksGroup.TaskType.PREVIOUS);
        checkAndSortTasks(tasksByGroupsMap, TasksGroup.TaskType.TODAY);
        checkAndSortTasks(tasksByGroupsMap, TasksGroup.TaskType.FUTURE);
        checkAndSortTasks(tasksByGroupsMap, TasksGroup.TaskType.DONE);

        tasksByGroups.clear();
        if (!tasksByGroupsMap.get(TasksGroup.TaskType.PREVIOUS).isEmpty())
            tasksByGroups.add(new TasksGroup(TasksGroup.TaskType.PREVIOUS, tasksByGroupsMap.get(TasksGroup.TaskType.PREVIOUS)));
        if (!tasksByGroupsMap.get(TasksGroup.TaskType.TODAY).isEmpty())
            tasksByGroups.add(new TasksGroup(TasksGroup.TaskType.TODAY, tasksByGroupsMap.get(TasksGroup.TaskType.TODAY)));
        if (!tasksByGroupsMap.get(TasksGroup.TaskType.FUTURE).isEmpty())
            tasksByGroups.add(new TasksGroup(TasksGroup.TaskType.FUTURE, tasksByGroupsMap.get(TasksGroup.TaskType.FUTURE)));
        if (!tasksByGroupsMap.get(TasksGroup.TaskType.DONE).isEmpty())
            tasksByGroups.add(new TasksGroup(TasksGroup.TaskType.DONE, tasksByGroupsMap.get(TasksGroup.TaskType.DONE)));

    }

    void checkAndSortTasks(Map<TasksGroup.TaskType, List<TaskItemClass>> tasksByGroups, TasksGroup.TaskType taskType)
    {
        if (!tasksByGroups.get(taskType).isEmpty())
            sortTasks(tasksByGroups.get(taskType));
    }

    void sortTasks(List<TaskItemClass> taskGroup)
    {
        taskGroup.sort(new Comparator<TaskItemClass>()
        {
            @Override
            public int compare(TaskItemClass o1, TaskItemClass o2)
            {
                Calendar o1date = o1.getDate() != null? o1.getDate() : Calendar.getInstance();
                Calendar o2date = o2.getDate() != null? o2.getDate() : Calendar.getInstance();
                if (equalOnlyDate(o1date, o2date))
                {
                    return (o1.positionInList > o2.positionInList) ? 1 : -1;
                }
                if (o1date.after(o2date))
                    return 1;
                else
                    return -1;
            }
        });
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

    int getLastPositionInListByDate(TaskItemClass task)
    {
        int positionInList = 0;

        Calendar currentDate = Calendar.getInstance();
        Calendar taskDate;
        if (task.getDate() == null)
        {
            taskDate = currentDate;
        }
        else
        {
            taskDate = task.getDate();
        }
        ArrayList<TaskItemClass> tasksByDate = new ArrayList<TaskItemClass>();

        for (TaskItemClass taskItem : tasks)
        {
            Calendar taskFromListDate = taskItem.getDate();
            if (taskFromListDate == null)
                taskFromListDate = currentDate;

            if (equalOnlyDate(taskFromListDate, taskDate))
            {
                tasksByDate.add(taskItem);
            }
        }

        for (TaskItemClass taskItem : tasksByDate)
        {
            if (taskItem.positionInList > positionInList)
                positionInList = taskItem.positionInList;
        }
        return positionInList;
    }
    @Override
    public void onAddNewTaskClicked(TaskItemClass taskItem)
    {
        if (taskItem.getTaskText() == "")
            return;

        taskItem.id = getFreeId();
        int lastPositionInList = getLastPositionInListByDate(taskItem);
        taskItem.positionInList = ++lastPositionInList;
        myDB.insertToDB(taskItem);

        refreshDataFromDb();
    }

    @Override
    public void onTaskClicked(Integer id, boolean checkedStatus) throws ParseException, InterruptedException
    {
        for (TaskItemClass task : tasks)
        {
            if (task.id == id)
            {
                task.status = checkedStatus;
                myDB.updateInDB(id, task);
            }
        }
        refreshDataFromDb();
    }

    @Override
    public void onTaskLayoutClicked(Integer id)
    {
        TaskItemClass task = null;
        for (int i = 0; i < tasks.size(); ++i)
        {
            if (tasks.get(i).getId() == id)
            {
                task = tasks.get(i);
                break;
            }
        }

        if (task != null)
        {
            Intent intent = new Intent(getContext(), EditTaskActivity.class);
            intent.putExtra("taskItem", task);
            intent.putExtra("listener_key", taskTreatmentListener);

            mStartForResult.launch(intent);
        }
    }
    public void onUpdateTaskClicked(TaskItemClass taskItem) throws ParseException {
        if (taskItem.getTaskText() == "")
            return;

        myDB.updateInDB(taskItem.getId(), taskItem);
        refreshDataFromDb();
    }


    public void onDeleteTaskClicked(TaskItemClass taskItem)
    {
        myDB.deleteInDb(taskItem.getId());
        refreshDataFromDb();
    }
}