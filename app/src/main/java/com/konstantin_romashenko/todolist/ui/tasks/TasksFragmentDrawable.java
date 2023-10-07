package com.konstantin_romashenko.todolist.ui.tasks;

import static com.konstantin_romashenko.todolist.ui.common.CalendarCommon.equalOnlyDate;
import static com.konstantin_romashenko.todolist.ui.common.TasksCommon.checkAndSortTasks;
import static com.konstantin_romashenko.todolist.ui.common.TasksCommon.sortTasks;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.konstantin_romashenko.todolist.R;
import com.konstantin_romashenko.todolist.databinding.FragmentTasksDrawableBinding;
import com.konstantin_romashenko.todolist.ui.common.TasksCommon;
import com.konstantin_romashenko.todolist.ui.db.MyDBManager;
import com.konstantin_romashenko.todolist.ui.dialog.TaskAddDialog;
import com.konstantin_romashenko.todolist.ui.tasks.callbacks.ItemTouchHelperCallback;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskAddListener;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskClickListener;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskDragListener;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskTreatmentListenerImpl;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import kotlinx.coroutines.scheduling.Task;


public class TasksFragmentDrawable extends Fragment implements View.OnClickListener, TaskClickListener, TaskAddListener, TaskDragListener, Serializable
{

    private TasksViewModel tasksViewModel;
    private TaskClickListener taskClickListener;
    private FragmentTasksDrawableBinding binding;
    private ExpandableListView tasksExpandableListView;
    private RecyclerView tasksRecyclerListView;
    ArrayList<TaskItemClass> previousTasks;
    ArrayList<TaskItemClass> todayTasks;
    ArrayList<TaskItemClass> futureTasks;
    ArrayList<TaskItemClass> doneTasks;

    private TasksArrayAdapterRecycler tasksAdapter;
    private MyDBManager myDB;
    private FloatingActionButton floatingActionButton;
    private TaskAddDialog taskAddDialog;

    private ArrayList<TaskItemClass> tasks;
    private Map<TasksCommon.TaskType, TasksGroup> tasksByGroups = new LinkedHashMap<>();
    private TaskTreatmentListenerImpl taskTreatmentListener;
    private Handler handler = new Handler(Looper.getMainLooper());

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

        binding = FragmentTasksDrawableBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        taskTreatmentListener = new TaskTreatmentListenerImpl(this);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tasksRecyclerListView = getView().findViewById(R.id.rvTasks);
        floatingActionButton = getView().findViewById(R.id.abCreateTask);
        floatingActionButton.setOnClickListener(this);

        tasksRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new TasksArrayAdapterRecycler(getActivity().getApplicationContext(), getLayoutInflater(), this, this);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(tasksAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(tasksRecyclerListView);

        Map<TasksCommon.TaskType, Boolean> expanded = new LinkedHashMap<>();
        expanded.put(TasksCommon.TaskType.TODAY, true);
        expanded.put(TasksCommon.TaskType.FUTURE, true);
        setExpanded(expanded);
        tasksRecyclerListView.setAdapter(tasksAdapter);
        myDB = new MyDBManager(getActivity().getApplicationContext());
        myDB.openDB();

        refreshDataFromDbFirstTime();
    }

    void refreshDataFromDb()
    {
        tasks = getDataFromDb();
        correctPositionInList(tasks);
        updateDataInDb();
        refreshRecyclerView(tasks, getExpanded(tasksByGroups.size()));
    }

    void refreshDataFromDbFirstTime()
    {
        tasks = getDataFromDb();
        checkOldTasks();
        correctPositionInList(tasks);
        updateDataInDb();

        Map<TasksCommon.TaskType, Boolean> expanded = new LinkedHashMap<>();
        expanded.put(TasksCommon.TaskType.TODAY, true);
        expanded.put(TasksCommon.TaskType.FUTURE, true);
        refreshRecyclerView(tasks, expanded);
    }
    void checkOldTasks()
    {
        ArrayList<TaskItemClass> tasksToDelete = new ArrayList<>();
        for (TaskItemClass task : tasks)
        {
            Calendar currentDate = Calendar.getInstance();
            Calendar date = task.getDate();
            long diffInMillis = currentDate.getTimeInMillis() - date.getTimeInMillis();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            if ((diffInDays > 30) && task.getStatus())
                tasksToDelete.add(task);
        }
        for (TaskItemClass taskToDelete : tasksToDelete)
        {
            tasks.remove(taskToDelete);
        }
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

    void correctPositionInListWithoutSort(List<TaskItemClass> tasks)
    {
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
    void refreshRecyclerView(ArrayList<TaskItemClass> tasks, Map<TasksCommon.TaskType, Boolean> expanded)
    {
        if (tasksAdapter != null)
        {
            tasksToGroups(tasks, tasksByGroups);
            setExpanded(expanded);
            tasksAdapter.updateAdapter(tasksByGroups);
            tasksRecyclerListView.setAdapter(tasksAdapter);
        }
    }

    Map<TasksCommon.TaskType, Boolean> getExpanded(int groupsCount)
    {
        Map<TasksCommon.TaskType, Boolean> expanded = new LinkedHashMap<>();
        for (TasksGroup taskGroup : tasksByGroups.values())
        {
            expanded.put(taskGroup.getGroupType(), taskGroup.isExpanded());
        }
        return expanded;
    }

    void setExpanded(Map<TasksCommon.TaskType, Boolean> expanded)
    {
        for (TasksCommon.TaskType taskType : expanded.keySet())
        {
            TasksGroup taskGroup = tasksByGroups.get(taskType);
            if (taskGroup != null)
            {
                taskGroup.setExpanded(expanded.get(taskType));
            }
        }
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

    public void tasksToGroups(ArrayList<TaskItemClass> taskItems, Map<TasksCommon.TaskType, TasksGroup> tasksByGroups )
    {
        Map<TasksCommon.TaskType, List<TaskItemClass>> tasksByGroupsMap = new HashMap<>();

        tasksByGroupsMap.clear();
        tasksByGroupsMap.put(TasksCommon.TaskType.DONE, new ArrayList<>());
        tasksByGroupsMap.put(TasksCommon.TaskType.FUTURE, new ArrayList<>());
        tasksByGroupsMap.put(TasksCommon.TaskType.TODAY, new ArrayList<>());
        tasksByGroupsMap.put(TasksCommon.TaskType.PREVIOUS, new ArrayList<>());

        for (TaskItemClass task : taskItems)
        {
            Calendar taskDate = task.getDate();

            Calendar currentDate = Calendar.getInstance();

            if (task.status)
            {
                task.setTaskType(TasksCommon.TaskType.DONE);
                tasksByGroupsMap.get(TasksCommon.TaskType.DONE).add(task);
            }
            else if (task.getDate() == null)
            {
                task.setTaskType(TasksCommon.TaskType.TODAY);
                tasksByGroupsMap.get(TasksCommon.TaskType.TODAY).add(task);
            }
            else if (equalOnlyDate(currentDate, task.getDate()))
            {
                task.setTaskType(TasksCommon.TaskType.TODAY);
                tasksByGroupsMap.get(TasksCommon.TaskType.TODAY).add(task);
            }
            else if (currentDate.getTime().after(task.getDate().getTime()))
            {
                task.setTaskType(TasksCommon.TaskType.PREVIOUS);
                tasksByGroupsMap.get(TasksCommon.TaskType.PREVIOUS).add(task);
            }
            else if (currentDate.getTime().before(task.getDate().getTime()))
            {
                task.setTaskType(TasksCommon.TaskType.FUTURE);
                tasksByGroupsMap.get(TasksCommon.TaskType.FUTURE).add(task);
            }
        }

        checkAndSortTasks(tasksByGroupsMap, TasksCommon.TaskType.PREVIOUS);
        checkAndSortTasks(tasksByGroupsMap, TasksCommon.TaskType.TODAY);
        checkAndSortTasks(tasksByGroupsMap, TasksCommon.TaskType.FUTURE);
        checkAndSortTasks(tasksByGroupsMap, TasksCommon.TaskType.DONE);

        tasksByGroups.clear();
        if (!tasksByGroupsMap.get(TasksCommon.TaskType.PREVIOUS).isEmpty())
            tasksByGroups.put(TasksCommon.TaskType.PREVIOUS,
                    new TasksGroup(TasksCommon.TaskType.PREVIOUS, tasksByGroupsMap.get(TasksCommon.TaskType.PREVIOUS)));
        if (!tasksByGroupsMap.get(TasksCommon.TaskType.TODAY).isEmpty())
            tasksByGroups.put(TasksCommon.TaskType.TODAY,
                    new TasksGroup(TasksCommon.TaskType.TODAY, tasksByGroupsMap.get(TasksCommon.TaskType.TODAY)));
        if (!tasksByGroupsMap.get(TasksCommon.TaskType.FUTURE).isEmpty())
            tasksByGroups.put(TasksCommon.TaskType.FUTURE,
                    new TasksGroup(TasksCommon.TaskType.FUTURE, tasksByGroupsMap.get(TasksCommon.TaskType.FUTURE)));
        if (!tasksByGroupsMap.get(TasksCommon.TaskType.DONE).isEmpty())
            tasksByGroups.put(TasksCommon.TaskType.DONE,
                    new TasksGroup(TasksCommon.TaskType.DONE, tasksByGroupsMap.get(TasksCommon.TaskType.DONE)));

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
    public void onDragFinished() throws ParseException {
        tasksByGroups = tasksAdapter.getTasksByGroups();
        for (TasksGroup group : tasksByGroups.values())
        {
            correctPositionInListWithoutSort(group.getTaskItems());
        }

        ArrayList<TaskItemClass> tasksToUpdateInDb = new ArrayList<>();
        for (TasksGroup group : tasksByGroups.values())
        {
            tasksToUpdateInDb.addAll(group.getTaskItems());
        }

        myDB.updateAllInDb(tasksToUpdateInDb);
        refreshDataFromDb();
    }

    class TaskCheckedDaemon implements Runnable
    {
        TasksFragmentDrawable tasksFragment;
        TaskCheckedDaemon(TasksFragmentDrawable tasksFragment)
        {
            super();
            this.tasksFragment = tasksFragment;
        }
        @Override
        public void run()
        {
            tasksFragment.refreshDataFromDb();
        }
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
        handler.postDelayed(new TaskCheckedDaemon(this), 500);

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