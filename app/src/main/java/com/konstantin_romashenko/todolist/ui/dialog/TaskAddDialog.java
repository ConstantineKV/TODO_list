package com.konstantin_romashenko.todolist.ui.dialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.konstantin_romashenko.todolist.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskAddListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Calendar;

public class TaskAddDialog extends DialogFragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    BottomSheetDialog bottomSheetDialog;
    TaskItemClass taskItem;
    FloatingActionButton addNewTaskButton;
    EditText edTaskText;
    ImageButton imDateTimeChoose;
    TextView tvDateValue;

    TextView tvTimeName;
    TextView tvTimeValue;
    TaskAddListener listener;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    LinearLayout llDate, llTime;

    public TaskAddDialog(Context context, TaskAddListener listener)
    {
        this.listener = listener;
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setTitle("New task");
        bottomSheetDialog.setContentView(R.layout.new_task_layout);
        taskItem = new TaskItemClass();
        int test = R.id.fbAddTask;

    }

    public void setId(Integer id)
    {
        taskItem.setId(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.new_task_layout, container);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        edTaskText = (EditText)view.findViewById(R.id.edNewTaskText);
        edTaskText.setText("");
        tvDateValue = (TextView)view.findViewById(R.id.tvDateValue);
        tvTimeName = (TextView)view.findViewById(R.id.tvTimeName);
        tvTimeValue = (TextView)view.findViewById(R.id.tvTimeValue);
        addNewTaskButton = (FloatingActionButton) view.findViewById(R.id.fbAddTask);
        addNewTaskButton.setOnClickListener(this);

        llDate = (LinearLayout)view.findViewById(R.id.llDate);
        llTime = (LinearLayout)view.findViewById(R.id.llTime);
        tvTimeValue.setEnabled(false);

        llTime.setEnabled(false);
        llDate.setOnClickListener(this);
        llTime.setOnClickListener(this);


        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(p);
        getDialog().getWindow().setGravity(Gravity.BOTTOM | Gravity.RIGHT);

    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.fbAddTask)
        {
            try
            {
                onClickAddTask();
            }
            catch (ParseException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (v.getId() == R.id.llDate)
        {
            onClickDateChoose();
        }
        if (v.getId() == R.id.llTime)
        {
            onClickTimeChoose();
        }
    }
    
    public void onClickAddTask() throws ParseException {

        String taskText = edTaskText.getText().toString();
        if (taskText.equals(""))
        {
            Log.i("TODO_Konstantin","Attempt to create empty task");
            Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.creating_empty_task), Toast.LENGTH_SHORT);
            return;
        }

        taskItem.setPositionInList(1);
        taskItem.setTaskText(taskText);
        taskItem.setStatus(false);
        listener.onAddNewTaskClicked(taskItem);
        this.dismiss();
    }
    
    public void onClickDateChoose()
    {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void onClickTimeChoose()
    {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(getContext(), this,calendar.get(Calendar.HOUR_OF_DAY) + 1,
                calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        taskItem.setTime(Calendar.getInstance());

        taskItem.getTime().set(Calendar.HOUR_OF_DAY, hourOfDay);
        taskItem.getTime().set(Calendar.MINUTE, minute);
        taskItem.setDateSet(true);
        tvTimeValue.setText(String.format("%s:%s", convertDateElement(hourOfDay), convertDateElement(minute)));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        taskItem.setDate(Calendar.getInstance());

        taskItem.getDate().set(Calendar.YEAR, year);
        taskItem.getDate().set(Calendar.MONTH, month);
        taskItem.getDate().set(Calendar.DAY_OF_MONTH, dayOfMonth);
        taskItem.setDateSet(true);
        tvDateValue.setText(String.format("%s-%s-%s", year, convertDateElement(month+1), convertDateElement(dayOfMonth)));
        llTime.setEnabled(true);
        tvTimeName.setEnabled(true);
        tvTimeValue.setEnabled(true);
    }

    String convertDateElement(int dateElement)
    {
        String tempMonth = new String("");
        if (dateElement <= 9)
            return new String("0" + String.valueOf(dateElement));
        else if (dateElement > 9)
            return new String(String.valueOf(dateElement));
        return new String("");
    }
}
