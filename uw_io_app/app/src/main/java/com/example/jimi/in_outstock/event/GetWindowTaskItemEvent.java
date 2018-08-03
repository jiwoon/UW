package com.example.jimi.in_outstock.event;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jimi.in_outstock.activity.ShowActivity;
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

/**
 * 获取仓口任务条目事件
 */
public class GetWindowTaskItemEvent {
    // 仓口Id
    private String windowId;
    private ArrayList<TaskInfo> historyTaskInfos;
    // 任务条目
    private int index;
    private static final int SUCCESS_NUM = 200;

    private Handler handler;
   /* private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            historyTaskInfos = (ArrayList<TaskInfo>) msg.obj;
            if(historyTaskInfos!=null){
                    MyApplication.setHistoryTaskInfos(historyTaskInfos);
                    Intent intent = new Intent(MyApplication.getContext(), ShowActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getContext().startActivity(intent);

            }else{
                Toast.makeText(MyApplication.getContext(), "获取仓口任务条目失败", Toast.LENGTH_SHORT).show();
            }
        }
    };*/


    /**
     * 启用线程请求
     */
    class getWindowTaskItemThread extends Thread{
        @Override
        public void run() {
            RequestParams params = new RequestParams(UrlData.getUrlGetwindowtaskitems());
            params.addBodyParameter("id",windowId);
            params.addBodyParameter("#TOKEN#", MyApplication.getToken());
            x.http().post(params, new Callback.CacheCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onSuccess(String result) {
                    result= new String(result.trim());
                    Log.d("GetWindow--onSuccess",result);
                    Message message = new Message();
                    message.obj = pareJSON(result);
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d("GetWindow--onError",ex.getMessage());
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
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void startGetWindowTaskItemThread(){
        new getWindowTaskItemThread().start();
    }

    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }



    /**
     * 数据解析
     * @param jsonData
     * @return
     */
    private ArrayList<TaskInfo> pareJSON(String jsonData){
        ArrayList<TaskInfo> historyTaskInfos = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            int result = jsonObject.getInt("result");
            if(result==SUCCESS_NUM){
                String data = jsonObject.getString("data");
                JSONObject dataJson = new JSONObject(data);
                String list = dataJson.getString("list");
                JSONArray lists = new JSONArray(list);
                index = lists.length();
                for(int i=0;i<lists.length();i++){
                    JSONObject listItem = lists.getJSONObject(i);
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setPlanQuantity(listItem.getInt("planQuantity"));
                    taskInfo.setActualQuantity(listItem.getInt("actualQuantity"));
                    taskInfo.setFinishTime(listItem.getString("finishTime"));
                    taskInfo.setTaskId(listItem.getInt("id"));
                    taskInfo.setType(listItem.getString("type"));
                    taskInfo.setFileName(listItem.getString("fileName"));
                    taskInfo.setMaterialNo(listItem.getString("materialNo"));
                    String details = listItem.getString("details");
                    ArrayList<MaterialPlateInfo> materialPlateInfos = pareJsonMps(details);
                    taskInfo.setMaterialPlateInfos(materialPlateInfos);
                    historyTaskInfos.add(taskInfo);
                }
                return historyTaskInfos;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
