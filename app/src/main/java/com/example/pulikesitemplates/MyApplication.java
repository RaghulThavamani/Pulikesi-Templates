package com.example.pulikesitemplates;

import android.app.Application;

public class MyApplication extends Application {

    public  static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance(){return instance;}
}
