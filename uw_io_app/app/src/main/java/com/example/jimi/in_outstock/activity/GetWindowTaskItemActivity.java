package com.example.jimi.in_outstock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                                    String windowId = edit_windowId.getText().toString();
                                    if (!isNum(windowId)) {
                                        Toast.makeText(MyApplication.getContext(), "仓口Id错误，请重新扫描仓口码", Toast.LENGTH_SHORT).show();
                                        edit_windowId.setText("");
                                        edit_windowId.requestFocus();
                                        return false;
                                    }
                                    if("".equals(windowId)){
                                        Toast.makeText(MyApplication.getContext(), "仓口Id为空，请重新扫描仓口码", Toast.LENGTH_SHORT).show();
                                        edit_windowId.requestFocus();
                                        return false;
                                    }else {
                                        MyApplication.setWindowId(strValue);
                                        Intent intent = new Intent(MyApplication.getContext(), ShowActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        MyApplication.getContext().startActivity(intent);
                                    }
                                    break;
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    private boolean isNum(String str){
        try{
            Integer i = Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
