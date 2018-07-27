package com.example.jimi.in_outstock.editTextListener;

import android.text.Editable;
import android.widget.EditText;

/**
 * 登录框监听器
 */
public class LoginEditText extends MyTextWatcher{
    // 当前输入框
    private EditText nowEditText;
    // 下一个输入框
    private EditText nextEditText;

    public LoginEditText(EditText editText1, EditText editText2){
        super(editText1);
        nowEditText = editText1;
        if(editText2!=null){
            nextEditText = editText2;
        }
    }

    /**
     * 输入回车，判断输入是否为空
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);
        String str = editable.toString();
        if (str.indexOf("/r") >= 0 || str.indexOf("\n") >= 0) {
            if(str.length()>0 && nextEditText!=null){
                nextEditText.requestFocus();
            }else{
                nowEditText.setSelection(nowEditText.getText().length());
            }
        }
    }
}
