package com.meme.pulikesitemplates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Objects;

public class IntroActivity extends AppCompatActivity {
    int SPLASH_TIME = 1000; //This is 1 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen
        setContentView(R.layout.activity_intro);

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String versionName = pinfo != null ? pinfo.versionName : null;
        TextView version = findViewById(R.id.versionName);
        version.setText(versionName);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    startActivity(new Intent(IntroActivity.this,MainActivity.class));
                    finish();
                    //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
                } catch (Exception ex) {
                    // Here we are logging the exception to see why it happened.
                    Log.e("my app", ex.toString());
                }


            }
        }, SPLASH_TIME);
    }
}