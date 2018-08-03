package com.example.jimi.in_outstock.activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jimi.in_outstock.fragment.HistoryFragment;
import com.example.jimi.in_outstock.fragment.RunningFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_show)
public class ShowActivity extends BaseActivity implements View.OnClickListener{
        @ViewInject(R.id.historyTV)
        private TextView historyTV;
        @ViewInject(R.id.runningTV)
        private TextView runningTV;

        private HistoryFragment historyFragment;

        private RunningFragment runningFragment;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show);
            bindView();
            runningTV.performClick();
        }

        private void  bindView(){
            historyTV.setOnClickListener(this);
            runningTV.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hide(transaction);
            switch (view.getId()){
                case R.id.historyTV:
                    runningTV.setSelected(false);
                    historyTV.setSelected(true);
                    if(historyFragment == null){
                        historyFragment = new HistoryFragment();
                        transaction.add(R.id.fragment_container,historyFragment);
                    }else{
                        transaction.show(historyFragment);
                    }
                    break;
                case  R.id.runningTV:
                    historyTV.setSelected(false);
                    runningTV.setSelected(true);
                    if(runningFragment == null){
                        runningFragment = new RunningFragment();
                        transaction.add(R.id.fragment_container,runningFragment);
                    }else {
                        transaction.show(runningFragment);
                    }
                    break;
            }
            transaction.commit();
        }

        private void hide(FragmentTransaction transaction){
            if (historyFragment != null){
                transaction.hide(historyFragment);
            }
            if (runningFragment != null){
                transaction.hide(runningFragment);
            }
        }
}

