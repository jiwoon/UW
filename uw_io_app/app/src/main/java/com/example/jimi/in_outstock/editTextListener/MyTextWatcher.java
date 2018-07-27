package com.example.jimi.in_outstock.editTextListener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * 文本监听器
 */
public class MyTextWatcher implements TextWatcher{
    private EditText myEditText;

    public MyTextWatcher(EditText editText){
        myEditText = editText;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    /**
     * 判断是否输入回车键，有则转换成空字符
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        String str = editable.toString();
        if (str.indexOf("/r") >= 0 || str.indexOf("\n") >= 0) {
            myEditText.setText(str.replace("/r", "").replace("\n", ""));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
}
