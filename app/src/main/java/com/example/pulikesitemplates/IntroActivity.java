package com.example.pulikesitemplates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class IntroActivity extends AppCompatActivity {
    int SPLASH_TIME = 1000; //This is 1 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(IntroActivity.this,MainActivity.class));
                finish();
                //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
            }
        }, SPLASH_TIME);

    }
}