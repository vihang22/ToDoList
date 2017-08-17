package com.ckeeda.todolist;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by HP on 16-Aug-17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
