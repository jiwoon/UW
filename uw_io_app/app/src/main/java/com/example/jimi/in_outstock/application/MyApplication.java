package com.example.jimi.in_outstock.application;

import android.app.Application;
import android.content.Context;

import com.example.jimi.in_outstock.entity.TaskInfo;

import org.xutils.x;

import java.util.ArrayList;

public class MyApplication extends Application{
    private static Context context;
    private static String token;
    // 仓口码
    private static String windowId;
    // 任务条目列表
    private static ArrayList<TaskInfo> historyTaskInfos;
    // 第一个未完成任务条目
    private static int position;

    private static TaskInfo parkingTaskInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        context =getApplicationContext();
        x.Ext.init(this);
    }

    public static Context getContext() {
        return context;
    }

    public static void setPosition(int position) {
        MyApplication.position = position;
    }

    public static int getPosition() {
        return position;
    }

    public static void setHistoryTaskInfos(ArrayList<TaskInfo> historyTaskInfos) {
        MyApplication.historyTaskInfos = historyTaskInfos;
    }

    public static ArrayList<TaskInfo> getHistoryTaskInfos() {
        return historyTaskInfos;
    }

    public static void setWindowId(String windowId) {
        MyApplication.windowId = windowId;
    }

    public static String getWindowId() {
        return windowId;
    }

    public static void setToken(String token) {
        MyApplication.token = token;
    }

    public static String getToken() {
        return token;
    }

    public synchronized static TaskInfo getParkingTaskInfo() { return parkingTaskInfo; }

    public synchronized static void setParkingTaskInfo(TaskInfo parkingTaskInfo) { MyApplication.parkingTaskInfo = parkingTaskInfo; }
}
