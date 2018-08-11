package com.example.jimi.in_outstock.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.TextView;

import com.example.jimi.in_outstock.activity.LoginActivity;
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

    //成功
    private static final int SUCCESS_NUM = 200;
    //网络异常(自定义)
    private static final int NETWORK_NUM = 0;
    //权限不足
    private static final int ACCESS_NUM = 401;
    //服务器内部错误
    private static final int SERVER_NUM = 500;
    //未知错误
    private static final int UNKNOW_NUM = 666666;

    @ViewInject(R.id.viewPager_history_task)
    public ViewPager viewPager_task;
    @ViewInject(R.id.tv_no_network)
    private TextView tv_no_network;
    @ViewInject(R.id.tv_tasklist)
    private TextView tv_tasklist;
    @ViewInject(R.id.bar_line)
    private TextView bar_line;


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
    @SuppressLint("HandlerLeak")
    public void update() {
        GetWindowTaskItemEvent getWindowTaskItem = new GetWindowTaskItemEvent();
        getWindowTaskItem.setWindowId(MyApplication.getWindowId());
        getWindowTaskItem.setHandler(new Handler(){
            public void handleMessage(Message msg) {
                int code = msg.what;
                if (code == SUCCESS_NUM) {
                    tv_no_network.setText("");
                    tv_no_network.setVisibility(View.GONE);
                    tv_tasklist.setVisibility(View.GONE);
                    bar_line.setVisibility(View.GONE);
                    viewPager_task.setVisibility(View.VISIBLE);
                    ArrayList<TaskInfo> historyTaskInfos = (ArrayList<TaskInfo>) msg.obj;
                    if (historyTaskInfos != null && historyTaskInfos.size() > 0) {
                        MyApplication.setHistoryTaskInfos(historyTaskInfos);
                        refleshFragment();
                    } else {
                        initFragment();
                    }
                }else if(code == NETWORK_NUM){
                    initFragment();
                    hideAndShow();
                    tv_no_network.setText("网络异常，无法连接服务器\n请点击页面重新刷新");
                    handler.removeCallbacks(runnable);
                }else if(code == ACCESS_NUM){
                    initFragment();
                    hideAndShow();
                    tv_no_network.setText("权限不足，请点击重新登录");
                    handler.removeCallbacks(runnable);
                }else if(code == SERVER_NUM){
                    initFragment();
                    hideAndShow();
                    tv_no_network.setText("服务器内部错误，请点击页面重新刷新");
                    handler.removeCallbacks(runnable);
                }else {
                    initFragment();
                    hideAndShow();
                    tv_no_network.setText("未知错误，请点击页面重新刷新");
                    handler.removeCallbacks(runnable);
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
        tv_no_network.setVisibility(View.GONE);
        tv_tasklist.setVisibility(View.GONE);
        bar_line.setVisibility(View.GONE);
        tv_no_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = tv_no_network.getText().toString();
                if (text.contains("请点击页面重新刷新")){
                    tv_no_network.setText("正在刷新，请稍后");
                    handler.postDelayed(runnable,1000);
                }else if(text.contains("请点击重新登录")){
                    Intent intent = new Intent(MyApplication.getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getContext().startActivity(intent);
                }
            }
        });
        handler.postDelayed(runnable, 0);
    }

    public void refleshFragment(){
        tv_no_network.setText("");
        tv_no_network.setVisibility(View.GONE);
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
            Integer j = Integer.valueOf(i+1);
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
        tv_no_network.setText("");
        tv_no_network.setVisibility(View.GONE);
        MyApplication.setPosition(-1);
        fragmentManager = getChildFragmentManager();
        fragments.clear();
        titleList.clear();
        fragments.add(new BlankFragment());
        titleList.add("任务-0");
        taskItemViewPagerAdapter = new TaskItemViewPagerAdapter(fragmentManager,fragments,titleList);
        viewPager_task.setAdapter(taskItemViewPagerAdapter);
        viewPager_task.setCurrentItem(MyApplication.getPosition());
    }

    private void hideAndShow(){
        viewPager_task.setVisibility(View.GONE);
        tv_no_network.setVisibility(View.VISIBLE);
        tv_tasklist.setVisibility(View.VISIBLE);
        bar_line.setVisibility(View.VISIBLE);
        tv_no_network.bringToFront();
        tv_tasklist.bringToFront();
        bar_line.bringToFront();
    }
    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
