package com.example.jimi.in_outstock.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jimi.in_outstock.activity.R;
import com.example.jimi.in_outstock.adpater.MaterialPlateListViewAdapter;
import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.entity.MaterialPlateInfo;
import com.example.jimi.in_outstock.entity.TaskInfo;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 已完成任务条目碎片
 */
@ContentView(R.layout.fragment_task)
public class TaskFragment extends Fragment{
    @ViewInject(R.id.listView_plate)
    private ListView listView_plate;
    @ViewInject(R.id.text_pQ)
    private TextView text_planQuantity;
    @ViewInject(R.id.text_aQ)
    private TextView text_actualQuantity;
    @ViewInject(R.id.text_finishTime)
    private TextView text_finishTime;
    @ViewInject(R.id.text_fileName)
    private TextView text_fileName;
    @ViewInject(R.id.text_materialNo)
    private TextView text_materialNo;
    @ViewInject(R.id.text_type)
    private TextView text_tpye;

    private TaskInfo taskInfo;
    private ArrayList<MaterialPlateInfo> materialPlateInfos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  x.view().inject(this, inflater, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 加载数据
     */
    public void initData(){
        text_planQuantity.setText(taskInfo.getPlanQuantity()+"");
        text_actualQuantity.setText(taskInfo.getActualQuantity()+"");
        text_finishTime.setText(taskInfo.getFinishTime());
        text_fileName.setText(taskInfo.getFileName());
        text_materialNo.setText(taskInfo.getMaterialNo());
        text_tpye.setText(taskInfo.getType());
        materialPlateInfos = taskInfo.getMaterialPlateInfos();
        MaterialPlateListViewAdapter materialPlateListViewAdapter = new MaterialPlateListViewAdapter(MyApplication.getContext(),R.layout.material_plate_item,materialPlateInfos);
        listView_plate.setAdapter(materialPlateListViewAdapter);
    }

    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }
}
