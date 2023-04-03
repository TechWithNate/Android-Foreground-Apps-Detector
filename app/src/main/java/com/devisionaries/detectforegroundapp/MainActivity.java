package com.devisionaries.detectforegroundapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ToggleButton startbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startbtn = (ToggleButton) findViewById(R.id.toggleButton);


        //Log.d(TAG, "onCreate: ");
//        Intent serviceIntent = new Intent(MainActivity.this, PlatformDetector.class);
//        startForegroundService(serviceIntent);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUsageStatsPermission()) {
//                     Permission has been granted, proceed with using UsageStatsManager
                    if(startbtn.isChecked()){
                        Intent stopServiceIntent = new Intent(MainActivity.this, PlatformDetector.class);
                        stopService(stopServiceIntent);
                    }else{
                        Intent serviceIntent = new Intent(MainActivity.this, PlatformDetector.class);
                        startForegroundService(serviceIntent);
                    }
                } else {
//                     Permission has not been granted, ask the user to grant the permission
                    requestUsageStatsPermission();
                }
            }
        });
    }

    private boolean checkUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}