package com.example.jimi.in_outstock.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.jimi.in_outstock.common.CheckPermissionUtils;
import com.example.jimi.in_outstock.editTextListener.LoginEditText;
import com.example.jimi.in_outstock.event.LoginEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.edit_userName)
    private EditText edit_userName;
    @ViewInject(R.id.edit_password)
    private EditText edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 监听用户名输入框
        edit_userName.addTextChangedListener(new LoginEditText(edit_userName,edit_password));
        // 监听密码输入框
        edit_password.addTextChangedListener(new LoginEditText(edit_password,null));
        CheckPermissionUtils.checkPermission(this);
    }

    /**
     * 点击按钮登录事件
     * @param view
     */
    @Event(value = {R.id.btn_submit})
    private void btn_login(View view){
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.login(edit_userName,edit_password);
    }
}
