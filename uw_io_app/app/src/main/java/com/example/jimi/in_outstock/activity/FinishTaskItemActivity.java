package com.example.jimi.in_outstock.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimi.in_outstock.adpater.TaskItemViewPagerAdapter;
import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.entity.MaterialPlateInfo;
import com.example.jimi.in_outstock.entity.TaskInfo;
import com.example.jimi.in_outstock.event.RobotBackEvent;
import com.example.jimi.in_outstock.event.WriteLogEvent;
import com.example.jimi.in_outstock.fragment.NowTaskFragment;
import com.example.jimi.in_outstock.fragment.TaskFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

@ContentView(R.layout.activity_finish_task_item)
public class FinishTaskItemActivity extends BaseActivity {
    @ViewInject(R.id.viewPager_task)
    private ViewPager viewPager_task;
    @ViewInject(R.id.edit_plateId)
    private EditText edit_plateId;

    private ArrayList<Fragment> fragments =new ArrayList<>();
    private ArrayList<TaskInfo> taskInfos;
    private TaskInfo taskInfo;
    private FragmentManager fragmentManager;
    private TaskItemViewPagerAdapter taskItemViewPagerAdapter;
    private String materialId;
    private int quantity;
    private String no;

    // 还未完成的任务条目的位置
    private ArrayList<Integer> positions = new ArrayList<>();
    private String TAG = "FinishTaskItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
        // 扫料盘
        edit_plateId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER)){
                    String strValue = String.valueOf(((EditText) textView).getText()).trim();
                    parePlateInfo(strValue);
                    edit_plateId.setText(no);
                    TaskInfo taskInfo = taskInfos.get(MyApplication.getPosition());
                    strValue = String.valueOf(((EditText) textView).getText()).trim();

                    // 判断扫描的料号信息和任务条目任务信息是否相同
                    if(!(strValue).equals(taskInfo.getMaterialNo())){
                        Toast.makeText(FinishTaskItemActivity.this,"料盘扫描错误",Toast.LENGTH_SHORT).show();
                        edit_plateId.setSelection(edit_plateId.getText().length());
                        editTextable(edit_plateId,true);
                    }else{
                        Toast.makeText(FinishTaskItemActivity.this,"料盘扫描正确",Toast.LENGTH_SHORT).show();
                        edit_plateId.setSelection(edit_plateId.getText().length());
                        editTextable(edit_plateId,false);
                        updateList();
                    }
                }
                return false;
            }
        });


       /* edit_plateId.addTextChangedListener(new MyTextWatcher(edit_plateId));
        edit_plateId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //回车键
                if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER)) {
                    switch (keyEvent.getAction()) {
                        //按下
                        case KeyEvent.ACTION_DOWN:
                            //扫描内容
                            String strValue = String.valueOf(((EditText) textView).getText());
                            strValue = strValue.replaceAll("\r", "");
                            parePlateInfo(strValue);
                            edit_plateId.setText(no);
                            Log.i(TAG, "strValue:" + strValue);
                            switch (textView.getId()){
                                case R.id.edit_plateId:
                                    TaskInfo taskInfo = taskInfos.get(MyApplication.getPosition());
                                    strValue = String.valueOf(((EditText) textView).getText());
                                    if (strValue.length() > 0) {
                                        if(!strValue.equals(taskInfo.getMaterialNo().trim())){
                                            Toast.makeText(FinishTaskItemActivity.this,"料盘扫描错误",Toast.LENGTH_SHORT).show();
                                        }else{

                                            }
                                        }
                                    }
                                    break;
                            }
                            break;
                    }
                }
                return false;
            }
        });*/
    }

    /**
     * 加载数据
     */
    public void initFragment(){
        MyApplication.setPosition(-1);
        taskInfos = MyApplication.getTaskInfos();
        fragmentManager = getSupportFragmentManager();
        for(int i=0;i<taskInfos.size();i++){
            // 判断完成时间是否为no
            if("no".equals(taskInfos.get(i).getFinishTime())){
                positions.add(i);
                NowTaskFragment fragment = new NowTaskFragment();
                fragment.setTaskInfo(taskInfos.get(i));
                fragments.add(fragment);
            }else{
                TaskFragment fragment = new TaskFragment();
                fragment.setTaskInfo(taskInfos.get(i));
                fragments.add(fragment);
            }
        }
        // 获取第一个未完成任务条目的位置
        if(positions.size()>0){
            MyApplication.setPosition(positions.get(0));
            taskInfo = taskInfos.get(MyApplication.getPosition());
        }
        taskItemViewPagerAdapter = new TaskItemViewPagerAdapter(fragmentManager,fragments);
        viewPager_task.setAdapter(taskItemViewPagerAdapter);
        viewPager_task.setCurrentItem(MyApplication.getPosition());
    }

    /**
     * ”操作完毕“按钮点击事件
     * @param view
     */
    @Event(value={R.id.btn_ok})
    private void btn_finishItem(View view){
        showExitDialog();
    }

    /**
     * 提示操作是否完毕
     */
    private void showExitDialog(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你确定操作完毕了吗?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Log.d("position",MyApplication.getPosition()+"");
                        if(MyApplication.getPosition()>=0){
                            new RobotBackEvent().robotBack(taskInfo.getTaskId());
                        }else{
                            showIsFinishDialog();
                        }
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    /**
     * 提示所有任务是否完成
     */
    private void showIsFinishDialog(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前该仓库任务条目已完成，是否返回前一个页面")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(FinishTaskItemActivity.this, GetWindowTaskItemActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    /**
     * 扫料盘信息解析
     */
    private void parePlateInfo(String str){
        String[] a = str.split("@");
        no = a[0];
        quantity = Integer.parseInt(a[1]);
        materialId = a[2];
        Log.d("no",no);
        Log.d("materialId",materialId);
        Log.d("quantity",quantity+"");
    }

    //设置EditText可输入和不可输入状态
    private void editTextable(EditText editText, boolean editable) {
        if (!editable) {
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setClickable(false);
        } else {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setClickable(true);
        }
    }

    //更新扫描后的列表信息
    private void updateList(){
        int position = MyApplication.getPosition();
        if(position != -1) {
            MaterialPlateInfo materialPlateInfo = new MaterialPlateInfo();
            materialPlateInfo.setMaterialId(materialId);
            materialPlateInfo.setQuantity(quantity);
            NowTaskFragment taskFragment = new NowTaskFragment();
            // 实际数量添加
            int aQ = taskInfo.getActualQuantity();
            aQ = aQ + quantity;
            taskInfo.setActualQuantity(aQ);
            // 新的料盘信息添加
            taskInfo.getMaterialPlateInfos().add(materialPlateInfo);
            taskFragment.setTaskInfo(taskInfo);
            fragments.set(MyApplication.getPosition(), taskFragment);
            taskItemViewPagerAdapter = new TaskItemViewPagerAdapter(fragmentManager, fragments);
            new WriteLogEvent().writeLog(taskInfo.getTaskId(), materialId, quantity, no, viewPager_task, taskItemViewPagerAdapter);
        }
    }
}
