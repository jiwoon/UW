package com.example.jimi.in_outstock.event;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

public class GetWindowParkingTaskItem {
    // 仓口Id
    private String windowId;
   // public  TaskInfo parkingTaskInfos;
    private Handler handler;
    // 任务条目
    private static final int SUCCESS_NUM = 200;

    public  GetWindowParkingTaskItem(){
        windowId = MyApplication.getWindowId();
    }

    public void setHandler(Handler handler){

        this.handler = handler;
    }

    public void startGetWindowParkingTaskItemThread(){
        new GetWindowParkingTaskItemThread().start();
    }

    class GetWindowParkingTaskItemThread extends Thread{
        @Override
        public void run() {
            RequestParams params = new RequestParams(UrlData.getUrlGetwindowparkingitem());
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
                    message.obj = paresParkingJSON(result);
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

    private TaskInfo paresParkingJSON(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            int result = jsonObject.getInt("result");
            if(result==SUCCESS_NUM){
                String data = jsonObject.getString("data");
                System.out.println("data" + data);
                TaskInfo taskInfo = new TaskInfo();
                JSONObject dataJson = new JSONObject(data);
                taskInfo.setPlanQuantity(dataJson.getInt("planQuantity"));
                taskInfo.setActualQuantity(dataJson.getInt("actualQuantity"));
                taskInfo.setTaskId(dataJson.getInt("id"));
                taskInfo.setType(dataJson.getString("type"));
                taskInfo.setFileName(dataJson.getString("fileName"));
                taskInfo.setMaterialNo(dataJson.getString("materialNo"));
                String details = dataJson.getString("details");
                ArrayList<MaterialPlateInfo> materialPlateInfos = pareJsonMps(details);
                taskInfo.setMaterialPlateInfos(materialPlateInfos);
                return taskInfo;
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
/*
    public void setParkingTaskInfos(TaskInfo parkingTaskInfos) {
        this.parkingTaskInfos = parkingTaskInfos;
    }*/
}
