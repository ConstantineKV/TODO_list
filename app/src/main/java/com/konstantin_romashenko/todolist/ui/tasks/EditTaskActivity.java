package com.konstantin_romashenko.todolist.ui.tasks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.konstantin_romashenko.todolist.R;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskAddListener;

import java.text.ParseException;
import java.util.Calendar;

import static com.konstantin_romashenko.todolist.ui.common.CalendarCommon.convertDateElement;
import com.konstantin_romashenko.todolist.ui.tasks.listeners.TaskTreatmentListener;

import org.jetbrains.annotations.NotNull;

public class EditTaskActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        Toolbar.OnMenuItemClickListener
{
    int id;
    String taskText;
    Calendar date;
    Calendar time;

    TaskItemClass taskItem;

    EditText etTaskText;
    TextView tvDateValue;
    TextView tvTimeName;
    TextView tvDateName;
    TextView tvTimeValue;
    TaskAddListener listener;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    LinearLayout llDate, llTime;
    Toolbar toolbar;
    TaskTreatmentListener taskTreatentListener;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.edit_task);

        init();
        receiveIntent();
        setDataFromIntent();
    }

    private void init()
    {
        toolbar = findViewById(R.id.tbEditTask);
        toolbar.setOnMenuItemClickListener(this);
        etTaskText = findViewById(R.id.etEditTaskText);
        llDate = findViewById(R.id.llEditTaskDate);
        llTime = findViewById(R.id.llEditTaskTime);
        tvTimeName = findViewById(R.id.tvEditTaskTimeName);
        tvDateName = findViewById(R.id.tvEditTaskDateName);
        tvDateValue = findViewById(R.id.tvEditTaskDateValue);
        tvTimeValue = findViewById(R.id.tvEditTaskTimeValue);
        llDate = (LinearLayout)findViewById(R.id.llEditTaskDate);
        llTime = (LinearLayout)findViewById(R.id.llEditTaskTime);
        llDate.setOnClickListener(this);
        llTime.setOnClickListener(this);

    }

    private void receiveIntent()
    {
        Intent intent = getIntent();
        taskItem = (TaskItemClass)intent.getSerializableExtra("taskItem");
        taskTreatentListener = (TaskTreatmentListener) intent.getSerializableExtra("listener_key");
    }

    private void setDataFromIntent()
    {
        if (taskItem != null)
        {
            etTaskText.setText(taskItem.getTaskText());
            if (taskItem.status)
            {
                toolbar.getMenu().getItem(0).setVisible(false);
            }
            else
            {
                toolbar.getMenu().getItem(1).setVisible(false);
            }

            if (taskItem.getDate() != null)
            {
                int year = taskItem.getDate().get(Calendar.YEAR);
                int month = taskItem.getDate().get(Calendar.MONTH);
                int dayOfMonth = taskItem.getDate().get(Calendar.DAY_OF_MONTH);
                taskItem.setDateSet(true);
                tvDateValue.setText(String.format("%s-%s-%s", year, convertDateElement(month+1), convertDateElement(dayOfMonth)));
                llTime.setEnabled(true);
                tvTimeName.setEnabled(true);
                tvTimeValue.setEnabled(true);
            }
            else
            {
                llTime.setEnabled(false);
                tvTimeName.setEnabled(false);
                tvTimeValue.setEnabled(false);
            }

            if (taskItem.getTime() != null)
            {
                llTime.setEnabled(true);
                tvTimeName.setEnabled(true);
                tvTimeValue.setEnabled(true);

                int hourOfDay = taskItem.getTime().get(Calendar.HOUR_OF_DAY);
                int minute = taskItem.getTime().get(Calendar.MINUTE);

                tvTimeValue.setText(String.format("%s:%s", convertDateElement(hourOfDay), convertDateElement(minute)));
            }

        }


    }

    public void onClickPrevious(View view) throws ParseException {
        //taskItem.setTaskText(String.valueOf(etTaskText.getText()));
        //taskTreatentListener.onUpdateTaskClicked(taskItem);
        taskItem.setTaskText(String.valueOf(etTaskText.getText()));
        Intent data = new Intent();
        data.putExtra("taskItem", taskItem);
        setResult(1, data);
        finish();
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
    public void onClick(View v)
    {
        if (v.getId() == R.id.llEditTaskDate)
        {
            onClickDateChoose();
        }
        if (v.getId() == R.id.llEditTaskTime)
        {
            onClickTimeChoose();
        }
    }

    public void onClickDateChoose()
    {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(EditTaskActivity.this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void onClickTimeChoose()
    {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(EditTaskActivity.this, this,calendar.get(Calendar.HOUR_OF_DAY) + 1,
                calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {

            if (item.getItemId() == R.id.mMarkAsDone && taskItem.status == false)
            {
                taskItem.status = true;
                taskItem.setTaskText(String.valueOf(etTaskText.getText()));
                Intent data = new Intent();
                data.putExtra("taskItem", taskItem);
                setResult(1, data);
                finish();
            }
            else if (item.getItemId() == R.id.mMarkAsUndone && taskItem.status == true)
            {
                taskItem.status = false;
                taskItem.setTaskText(String.valueOf(etTaskText.getText()));
                Intent data = new Intent();
                data.putExtra("taskItem", taskItem);
                setResult(1, data);
                finish();
            }
            else if (item.getItemId() == R.id.mDeleteTask)
            {
                Intent data = new Intent();
                data.putExtra("taskItem", taskItem);
                setResult(2, data);
                finish();
            }
        return false;
    }
}