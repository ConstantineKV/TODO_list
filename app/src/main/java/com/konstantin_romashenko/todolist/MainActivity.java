package com.konstantin_romashenko.todolist;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.konstantin_romashenko.todolist.databinding.ActivityMainBinding;
import com.konstantin_romashenko.todolist.ui.tasks.TaskItemClass;
import com.konstantin_romashenko.todolist.ui.tasks.TasksArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
    private ListView tasksListView;
    private String[] str_arr;
    private ArrayAdapter<String> str_adapter;
    private TasksArrayAdapter tasksArrayAdapter;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setFlags(android.R.style.Theme_Material_NoActionBar_Fullscreen,
        //        android.R.style.Theme_Material_NoActionBar_Fullscreen);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_tasks, R.id.navigation_calendar)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }


    public void onClickPrevious(View view)
    {
    }

    public void onClickCreateTask(View view)
    {
    }
}