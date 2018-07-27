package com.example.jimi.in_outstock.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.editTextListener.MyTextWatcher;
import com.example.jimi.in_outstock.event.GetWindowTaskItemEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_get_window_task_item)
public class GetWindowTaskItemActivity extends BaseActivity {
    @ViewInject(R.id.edit_windowId)
    private EditText edit_windowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 监听仓口码输入框
        edit_windowId.addTextChangedListener(new MyTextWatcher(edit_windowId));
        edit_windowId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //回车键
                if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER)) {
                    switch (keyEvent.getAction()) {
                        //按下
                        case KeyEvent.ACTION_DOWN:
                            //扫描内容
                            String strValue = String.valueOf(((EditText) textView).getText());
                            strValue = strValue.trim();
                            switch (textView.getId()){
                                case R.id.edit_windowId:
                                    Log.d("windowId",strValue);
                                    GetWindowTaskItemEvent getWindowTaskItemEvent = new GetWindowTaskItemEvent();
                                    getWindowTaskItemEvent.getWindowTaskItem(edit_windowId);
                                    MyApplication.setWindowId(strValue);
                                    edit_windowId.setText("");
                                    break;
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }
}
