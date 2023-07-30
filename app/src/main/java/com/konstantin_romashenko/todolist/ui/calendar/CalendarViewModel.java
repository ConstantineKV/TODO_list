package com.konstantin_romashenko.todolist.ui.calendar;

import android.widget.RadioButton;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CalendarViewModel extends ViewModel
{

    private MutableLiveData<String> mText;

    public CalendarViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("This is calendar fragment");
    }

    public LiveData<String> getText()
    {
        return mText;
    }


}