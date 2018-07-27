package com.example.jimi.in_outstock.event;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.jimi.in_outstock.activity.FinishTaskItemActivity;
import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.common.UrlData;
import com.example.jimi.in_outstock.entity.MaterialPlateInfo;
import com.example.jimi.in_outstock.entity.TaskInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 刷新任务条目事件
 */
public class RefreshTaskEvent {
    // 仓口Id
    private String windowId;
    private ArrayList<TaskInfo> taskInfos;
    private Timer timer;

    public void refreshTask(Timer myTimer){
        windowId = MyApplication.getWindowId();
        timer = myTimer;
        Log.d("windowId",windowId);
        if(!"".equals(windowId)){
            new RefreshTaskThread().start();
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            taskInfos = (ArrayList<TaskInfo>) msg.obj;
            if(!"no".equals(taskInfos.get(MyApplication.getPosition()).getFinishTime())){
                timer.cancel();
                timer = null;
                Toast.makeText(MyApplication.getContext(),"叉车回库成功",Toast.LENGTH_SHORT).show();
                MyApplication.setTaskInfos(taskInfos);
                Intent intent = new Intent(MyApplication.getContext(),FinishTaskItemActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                MyApplication.getContext().startActivity(intent);
            }
        }
    };

    /**
     * 启用线程请求
     */
    class RefreshTaskThread extends Thread{

        @Override
        public void run() {
            RequestParams params = new RequestParams(UrlData.getUrlGetwindowtaskitems());
            params.addBodyParameter("id",windowId);
            params.addBodyParameter("#TOKEN#",MyApplication.getToken());
            x.http().post(params, new Callback.CacheCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onSuccess(String result) {
                    result= new String(result.trim());
                    Log.d("RefreshTask--onSuccess",result);
                    Message message = new Message();
                    message.obj = pareJSON(result);
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d("RefreshTask--onError",ex.getMessage());
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
    private ArrayList<TaskInfo> pareJSON(String jsonData){
        ArrayList<TaskInfo> taskInfos = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            String data = jsonObject.getString("data");
            JSONObject dataJson = new JSONObject(data);
            String list = dataJson.getString("list");
            JSONArray lists = new JSONArray(list);
            for(int i=0;i<lists.length();i++){
                JSONObject listItem = lists.getJSONObject(i);
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setPlanQuantity(listItem.getInt("planQuantity"));
                taskInfo.setActualQuantity(listItem.getInt("actualQuantity"));
                taskInfo.setFinishTime(listItem.getString("finishTime"));
                taskInfo.setTaskId(listItem.getInt("id"));
                taskInfo.setFileName(listItem.getString("fileName"));
                taskInfo.setMaterialNo(listItem.getString("materialNo"));
                String details = listItem.getString("details");
                ArrayList<MaterialPlateInfo> materialPlateInfos = pareJsonMps(details);
                taskInfo.setMaterialPlateInfos(materialPlateInfos);
                taskInfos.add(taskInfo);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return taskInfos;
    }

    /**
     * 料盘信息解析
     * @param mps
     * @return
     */
    private ArrayList<MaterialPlateInfo> pareJsonMps(String mps){
        ArrayList<MaterialPlateInfo> materialPlateInfos = new ArrayList<>();
        try{
            JSONArray lists = new JSONArray(mps);
            for(int i=0;i<lists.length();i++){
                JSONObject listItem = lists.getJSONObject(i);
                MaterialPlateInfo materialPlateInfo = new MaterialPlateInfo();
                materialPlateInfo.setMaterialId(listItem.getString("materialId"));
                materialPlateInfo.setQuantity(listItem.getInt("quantity"));
                materialPlateInfos.add(materialPlateInfo);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return materialPlateInfos;
    }
}
