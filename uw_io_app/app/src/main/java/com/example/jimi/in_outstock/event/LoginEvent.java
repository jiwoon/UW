package com.example.jimi.in_outstock.event;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jimi.in_outstock.activity.GetWindowTaskItemActivity;
import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.common.UrlData;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 登录事件
 */
public class LoginEvent {
    private EditText edit_userName;
    private EditText edit_password;
    private String userName;
    private String password;
    // 返回成功
    private static final int SUCCESS_NUM = 200;
    // 返回失败
    private static final int FAILL_NUM = 0;

    public void login(EditText editText_userName, EditText editText_password) {
        edit_userName = editText_userName;
        edit_password = editText_password;
        userName = edit_userName.getText().toString();
        password = edit_password.getText().toString();

        if ("".equals(userName)) {
            Toast.makeText(MyApplication.getContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
            edit_userName.requestFocus();
        } else if ("".equals(password)) {
            Toast.makeText(MyApplication.getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            edit_password.requestFocus();
        } else {
            new LoginThread().start();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS_NUM:
                    edit_userName.setText("");
                    edit_password.setText("");
                    Intent intent = new Intent(MyApplication.getContext(),GetWindowTaskItemActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    MyApplication.getContext().startActivity(intent);
                    break;
                case FAILL_NUM:
                    Toast.makeText(MyApplication.getContext(),"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 启用线程请求
     */
    class LoginThread extends Thread{

        @Override
        public void run() {
            RequestParams params = new RequestParams(UrlData.getUrlLogin());
            params.addBodyParameter("uid",userName);
            params.addBodyParameter("password",password);
            x.http().post(params, new Callback.CacheCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onSuccess(String result) {
                    result= new String(result.trim());
                    Log.d("Login--onSuccess",result);
                    Message message = new Message();
                    message.what = pareJSON(result);
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d("Login--onError :",ex.getMessage());
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
    private int pareJSON(String jsonData){
        int resultId = FAILL_NUM;
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            resultId = jsonObject.getInt("result");
            if(resultId == SUCCESS_NUM){
                String data= jsonObject.getString("data");
                JSONObject dataDetail = new JSONObject(data);
                String token = dataDetail.getString("#TOKEN#");
                if(!"".equals(token)){
                    MyApplication.setToken(token);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultId;
    }

}
