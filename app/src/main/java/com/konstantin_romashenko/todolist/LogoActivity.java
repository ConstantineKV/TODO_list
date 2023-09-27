package com.konstantin_romashenko.todolist;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;


public class LogoActivity extends AppCompatActivity
{
    AnimationDrawable logoAnimation;
    CountDownTimer timer;
    private ImageSwitcher mImageSwitcher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_layout);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        ImageView logoImage = (ImageView) findViewById(R.id.logoIV);
        logoImage.setBackgroundResource(R.drawable.logo_animation);
        logoAnimation = (AnimationDrawable) logoImage.getBackground();
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logoImage, View.ALPHA, 0f, 1f);
        fadeIn.setDuration(1000);

        logoAnimation.start();
        fadeIn.start();

        timer = new CountDownTimer(1500, 500) {
            @Override
            public void onTick(long millisUntilFinished)
            {

            }

            @Override
            public void onFinish()
            {
                startActivity(intent);
            }
        };
        timer.start();

    }
}
