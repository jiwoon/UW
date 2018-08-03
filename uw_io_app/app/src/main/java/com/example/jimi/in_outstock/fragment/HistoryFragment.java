package com.example.jimi.in_outstock.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jimi.in_outstock.activity.R;
import com.example.jimi.in_outstock.adpater.TaskItemViewPagerAdapter;
import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.entity.TaskInfo;
import com.example.jimi.in_outstock.event.GetWindowTaskItemEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.fragment_history)
public class HistoryFragment extends Fragment {

    @ViewInject(R.id.viewPager_history_task)
    public ViewPager viewPager_task;
    public  ArrayList<Fragment> fragments =new ArrayList<>();
    public FragmentManager fragmentManager;
    private ArrayList<TaskInfo> historyTaskInfos;
    private ArrayList<String> titleList = new ArrayList<>();
    private TaskInfo taskInfo;
    public TaskItemViewPagerAdapter taskItemViewPagerAdapter;

    // 还未完成的任务条目的位置
    private ArrayList<Integer> positions = new ArrayList<>();
    private String TAG = "FinishTaskItemActivity";

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            update();
            handler.postDelayed(this, 10000);// 间隔10秒
        }
    };

    /**
     * 发送http请求并根据回复刷新界面
     */
    public void update() {
        GetWindowTaskItemEvent getWindowTaskItem = new GetWindowTaskItemEvent();
        getWindowTaskItem.setWindowId(MyApplication.getWindowId());
        getWindowTaskItem.setHandler(new Handler(){
            public void handleMessage(Message msg) {
                ArrayList<TaskInfo> historyTaskInfos = (ArrayList<TaskInfo>) msg.obj;
                if(historyTaskInfos !=null && historyTaskInfos.size()>0){
                    MyApplication.setHistoryTaskInfos(historyTaskInfos);
                    refleshFragment();
                }else{
                    initFragment();
                }
            }
        });
        getWindowTaskItem.startGetWindowTaskItemThread();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
                    update();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager_task.setPageMargin(10);
        initFragment();
        handler.postDelayed(runnable, 0);
    }

    public void refleshFragment(){
        MyApplication.setPosition(-1);
        historyTaskInfos = MyApplication.getHistoryTaskInfos();
        fragmentManager = getChildFragmentManager();
        ArrayList<Fragment> no_Finish_Fragments = new ArrayList<>();
        ArrayList<Fragment> finish_Fragments = new ArrayList<>();
        no_Finish_Fragments.clear();
        finish_Fragments.clear();
        titleList.clear();
        fragments.clear();
        positions.clear();
        for(int i=0;i<historyTaskInfos.size();i++){
            // 判断完成时间是否为no
            if("no".equals(historyTaskInfos.get(i).getFinishTime())){
                positions.add(i);
                NowTaskFragment fragment = new NowTaskFragment();
                fragment.setTaskInfo(historyTaskInfos.get(i));
                no_Finish_Fragments.add(fragment);
            }else{
                TaskFragment fragment = new TaskFragment();
                fragment.setTaskInfo(historyTaskInfos.get(i));
                finish_Fragments.add(fragment);
            }
            Integer j = new Integer(i+1);
            titleList.add(("任务-"+j.toString()));
        }
        //将已完成的刚在前面，未完成的放在后面
        fragments.addAll(finish_Fragments);
        fragments.addAll(no_Finish_Fragments);
        // 获取第一个未完成任务条目的位置
        if(no_Finish_Fragments.size()>0){
            MyApplication.setPosition(finish_Fragments.size());
            taskInfo = historyTaskInfos.get(MyApplication.getPosition());
        }
        taskItemViewPagerAdapter = new TaskItemViewPagerAdapter(fragmentManager,fragments,titleList);
        viewPager_task.setAdapter(taskItemViewPagerAdapter);
        viewPager_task.setCurrentItem(MyApplication.getPosition());
    }
    /**
     * 加载数据
     */
    public void initFragment(){
        MyApplication.setPosition(-1);
        /*historyTaskInfos = MyApplication.getHistoryTaskInfos();
        fragmentManager = getChildFragmentManager();
        for(int i=0;i<historyTaskInfos.size();i++){
            // 判断完成时间是否为no
            if("no".equals(historyTaskInfos.get(i).getFinishTime())){
                positions.add(i);
                NowTaskFragment fragment = new NowTaskFragment();
                fragment.setTaskInfo(historyTaskInfos.get(i));
                fragments.add(fragment);
            }else{
                TaskFragment fragment = new TaskFragment();
                fragment.setTaskInfo(historyTaskInfos.get(i));
                fragments.add(fragment);
            }
        }
        // 获取第一个未完成任务条目的位置
        if(positions.size()>0){
            MyApplication.setPosition(positions.get(0));
            taskInfo = historyTaskInfos.get(MyApplication.getPosition());
        }*/
        fragmentManager = getChildFragmentManager();
        fragments.clear();
        titleList.clear();
        fragments.add(new BlankFragment());
        titleList.add("任务-0");
        taskItemViewPagerAdapter = new TaskItemViewPagerAdapter(fragmentManager,fragments,titleList);
        viewPager_task.setAdapter(taskItemViewPagerAdapter);
        viewPager_task.setCurrentItem(MyApplication.getPosition());
    }

}
