package com.devisionaries.detectforegroundapp;

import static com.devisionaries.detectforegroundapp.App.CHANNEL_ID;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PlatformDetector extends Service {

    public final String TAG = "PlatformDetector";

    public PlatformDetector() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: Creating Notification");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Platform Detector")
                .setContentText("Detecting Platform")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        Log.i(TAG, "onCreate: Created notificationIntent");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service started");
        start();
        return START_STICKY;
    }

    private void start(){
        Log.d(TAG, "start: Start function called");
        refresh(5000);
    }


    private void refresh(int milliseconds) {
        Log.d(TAG, "refresh: Refresh function called");
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                long currentTime = System.currentTimeMillis();
                List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 24 * 60 * 60 * 1000, currentTime);
                System.out.println("Outside If statement");
                if (usageStatsList != null && !usageStatsList.isEmpty()) {
                    System.out.println("Inside If statement");
                    UsageStats mostRecentUsageStats = null;
                    for (UsageStats usageStats : usageStatsList) {
                        if (mostRecentUsageStats == null || usageStats.getLastTimeUsed() > mostRecentUsageStats.getLastTimeUsed()) {
                            mostRecentUsageStats = usageStats;
                        }
                    }
                    try {
                        if (mostRecentUsageStats != null && !mostRecentUsageStats.getPackageName().equals(getPackageName())) {
                            PackageManager packageManager = PlatformDetector.this.getPackageManager();
                            if (packageManager!= null){
                                ApplicationInfo appInfo = packageManager.getApplicationInfo(mostRecentUsageStats.getPackageName(), PackageManager.GET_META_DATA);
                                String appName = (String) packageManager.getApplicationLabel(appInfo);
                                Log.d(TAG, "run:: "+appName);
                            }


                            String foregroundTaskAppName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(mostRecentUsageStats.getPackageName(), PackageManager.GET_META_DATA)).toString();

                            System.out.println("ForegroundTaskAppName0: "+ foregroundTaskAppName);

                            String[] name = foregroundTaskAppName.split("\\.");
                            Log.d(TAG, "run: "+name[name.length -1]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ForegroundTaskPackageName1:" + mostRecentUsageStats.getPackageName());
                        String[] name = mostRecentUsageStats.getPackageName().split("\\.");
                        Log.d(TAG, "run: "+name[name.length -1]);
                        Log.d(TAG, "run: Error in getting name from the package:", e);
                    }
                    start();
                }
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

//    private void refresh(int milliseconds){
//        Log.d(TAG, "refresh: Refresh function called");
//        final Handler handler = new Handler();
//
//        final Runnable runnable = new Runnable(){
//
//            @Override
//            public void run() {
//                try{
////                    ActivityManager am = (ActivityManager) PlatformDetector.this.getSystemService(ACTIVITY_SERVICE);
////                    // The first in the list of RunningTasks is always the foreground task.
////                    List<ActivityManager.RunningAppProcessInfo> appsList = am.getRunningAppProcesses();
////
////                    Iterator<ActivityManager.RunningAppProcessInfo> i = appsList.iterator();
////                    while(i.hasNext()){
////                        ActivityManager.RunningAppProcessInfo info = i.next();
////                        System.out.println("Info: "+ info);
////                    }
////                    System.out.println("run: Apps List is: " + Arrays.toString(appsList.toArray()));
//
//
//                    ActivityManager am = (ActivityManager) PlatformDetector.this.getSystemService(ACTIVITY_SERVICE);
//                    // The first in the list of RunningTasks is always the foreground task.
////                    Log.d(TAG, "run: ForegroundTaskInfo:", am.getRunningTasks(1));
//                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(10).get(0);
//                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
//                    PackageManager packageManager = PlatformDetector.this.getPackageManager();
//                    PackageInfo foregroundAppPackageInfo = packageManager.getPackageInfo(foregroundTaskPackageName, 0);
//                    String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(packageManager).toString();
//                    System.out.println("ForegroundTaskAppName: "+ foregroundTaskAppName);
//                }catch (Exception e){
//                    e.printStackTrace();
//                    Log.d(TAG, "run: Error in getting name from the package:", e);
//                }
//                start();
//            }
//        };
//        handler.postDelayed(runnable, milliseconds);
//    }
}