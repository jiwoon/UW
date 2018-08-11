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
    // 返回成功
    private static final int SUCCESS_NUM = 200;
    //网络异常(自定义)
    private static final int NETWORK_NUM = 0;
    //权限不足
    private static final int ACCESS_NUM = 401;
    //服务器内部错误
    private static final int SERVER_NUM = 500;
    //未知错误
    private static final int UNKNOW_NUM = 666666;

    private Handler handler;



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
                    Log.d("GetTask--onSuccess",result);
                    Message message = new Message();
                    message.obj = pareJSON(result);
                    message.what = getCode(result);
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    com.example.jimi.in_outstock.common.Log.d("GetTask--onError",ex.getMessage());
                    Message message = new Message();
                    message.what = NETWORK_NUM;
                    handler.sendMessage(message);
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

    private int getCode(String jsonData){
        int code = UNKNOW_NUM;
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            code = jsonObject.getInt("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        return  code;
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
