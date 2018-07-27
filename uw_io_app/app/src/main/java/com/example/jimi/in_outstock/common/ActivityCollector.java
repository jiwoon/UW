package com.example.jimi.in_outstock.common;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Activity管理器
 */
public class ActivityCollector {
    public static ArrayList<Activity> activities = new ArrayList<>();

    // 添加单个Activity
    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    // 删除单个Activity
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    // 删除所有Activity
    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
