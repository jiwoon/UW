package com.example.jimi.in_outstock.event;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.common.Log;
import com.example.jimi.in_outstock.common.UrlData;
import com.example.jimi.in_outstock.entity.TaskInfo;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 出入库任务日志写入事件
 */
public class WriteLogEvent {
    private int taskId;
    private int quantity;
    private String materialId;
    private String no;
    private TaskInfo taskInfo;
    public static final int SUCCESS_NUM = 200;
    //网络异常(自定义)
    private static final int NETWORK_NUM = 0;
    //权限不足
    private static final int ACCESS_NUM = 401;
    //服务器内部错误
    private static final int SERVER_NUM = 500;
    //料盘冲突
    private static final int USER_NUM = 412;
    //未知错误
    private static final int UNKNOW_NUM = 666666;

    public void writeLog(TaskInfo myTaskInfo, String myMaterialId, int myQuantity, String myNo){
        taskId = myTaskInfo.getTaskId();
        quantity = myQuantity;
        materialId = myMaterialId;
        no = myNo;
        taskInfo = myTaskInfo;
        if(taskId>=0&&!"".equals(materialId)&&quantity>=0){
            new WriteLogThread().start();
        }
    }

    @android.annotation.SuppressLint("HandlerLeak") private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case SUCCESS_NUM:
                    Toast.makeText(MyApplication.getContext(),"料盘添加成功",Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_NUM:
                    Toast.makeText(MyApplication.getContext(),"料盘添加失败，网络异常",Toast.LENGTH_LONG).show();
                    break;
                case ACCESS_NUM:
                    Toast.makeText(MyApplication.getContext(),"料盘添加失败，权限不足，请重新登录",Toast.LENGTH_LONG).show();
                    break;
                case USER_NUM:
                    String tips = (String)msg.obj;
                    if (tips.contains("时间戳为")&&tips.contains("的料盘已在同一个任务中被扫描过，请勿在同一个出入库任务中重复扫描同一个料盘！")){
                        Toast.makeText(MyApplication.getContext(), "料盘已经添加，请勿重复添加", Toast.LENGTH_LONG).show();
                    }else if (tips.contains("时间戳为")&&tips.contains("的料盘已入过库，请勿重复入库！")){
                        Toast.makeText(MyApplication.getContext(), "料盘已入库，请勿重复入库", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(MyApplication.getContext(), tips, Toast.LENGTH_LONG).show();
                    }
                    break;
                case SERVER_NUM:
                    Toast.makeText(MyApplication.getContext(),"料盘添加失败，服务器内部原因",Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(MyApplication.getContext(),"未知错误，请联系管理员",Toast.LENGTH_LONG).show();
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
                    message.what = getCode(result);
                    message.obj = pareJSON(result);
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d("WriteIO--onError",ex.getMessage());
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

    /**
     * 数据解析
     * @param jsonData
     * @return
     */
    private int  getCode(String jsonData){
        int result = UNKNOW_NUM;
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            result = jsonObject.getInt("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        return  result;
    }

    private String  pareJSON(String jsonData){
        String data = null;
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            data = jsonObject.getString("data");

        }catch (Exception e){
            e.printStackTrace();
        }
        return  data;
    }

  /*  //更新扫描后的列表信息
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
        }
    }*/
}
