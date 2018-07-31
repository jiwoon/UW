package com.example.jimi.in_outstock.event;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.example.jimi.in_outstock.adpater.TaskItemViewPagerAdapter;
import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.common.UrlData;
import com.example.jimi.in_outstock.entity.MaterialPlateInfo;
import com.example.jimi.in_outstock.entity.TaskInfo;
import com.example.jimi.in_outstock.fragment.NowTaskFragment;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 出入库任务日志写入事件
 */
public class WriteLogEvent {
    private int taskId;
    private int quantity;
    private String materialId;
    private String no;
    private ViewPager viewPager_task;
    private FragmentManager fragmentManager;
    private ArrayList<Fragment> fragments;
    private TaskInfo taskInfo;
    private static final int SUCCESS_NUM = 200;
    private static final int FAILL_NUM = 0;

    public void writeLog(TaskInfo myTaskInfo, String myMaterialId, int myQuantity, String myNo, ViewPager viewPager, FragmentManager fm, ArrayList<Fragment> myFragments){
            taskId = myTaskInfo.getTaskId();
            quantity = myQuantity;
            materialId = myMaterialId;
            no = myNo;
            viewPager_task = viewPager;
            taskInfo = myTaskInfo;
            fragmentManager = fm;
            fragments = myFragments;
            if(taskId>=0&&!"".equals(materialId)&&quantity>=0){
                new WriteLogThread().start();
            }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS_NUM:
                    updateList();
                    break;
                default:
                    Toast.makeText(MyApplication.getContext(),"未知错误，请联系管理员",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 启用线程请求
     */
    class WriteLogThread extends Thread{

        @Override
        public void run() {
            RequestParams params = new RequestParams(UrlData.getUrlWriteio());
            params.addBodyParameter("packListItemId",taskId+"" );
            params.addBodyParameter("materialId",materialId);
            params.addBodyParameter("quantity", quantity+"");
            params.addBodyParameter("#TOKEN#", MyApplication.getToken());
            params.addBodyParameter("no",no);
            x.http().post(params, new Callback.CacheCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onSuccess(String result) {
                    result= new String(result.trim());
                    Log.d("WriteIO--onSuccess",result);
                    Message message = new Message();
                    message.what = pareJSON(result);
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d("WriteIO--onError",ex.getMessage());
                }

                @Override
                public void onFinished() {

                }

                @Override
                public boolean onCache(String result) {
                    return false;
                }
            });

        }
    }

    /**
     * 数据解析
     * @param jsonData
     * @return
     */
    private int  pareJSON(String jsonData){
        int result = FAILL_NUM;
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            result = jsonObject.getInt("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        return  result;
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
            TaskItemViewPagerAdapter taskItemViewPagerAdapter = new TaskItemViewPagerAdapter(fragmentManager, fragments);
            viewPager_task.setAdapter(taskItemViewPagerAdapter);
            viewPager_task.setCurrentItem(MyApplication.getPosition());
        }
    }
}
