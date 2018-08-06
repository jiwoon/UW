package com.example.jimi.in_outstock.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimi.in_outstock.activity.R;
import com.example.jimi.in_outstock.adpater.MaterialPlateListViewAdapter;
import com.example.jimi.in_outstock.application.MyApplication;
import com.example.jimi.in_outstock.editTextListener.MyTextWatcher;
import com.example.jimi.in_outstock.entity.MaterialPlateInfo;
import com.example.jimi.in_outstock.entity.TaskInfo;
import com.example.jimi.in_outstock.event.GetWindowParkingTaskItem;
import com.example.jimi.in_outstock.event.RobotBackEvent;
import com.example.jimi.in_outstock.event.WriteLogEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.fragment_running)
public class RunningFragment extends Fragment {

    @ViewInject(R.id.edit_plateId)
    private EditText edit_plateId;
    @ViewInject(R.id.listView_plate)
    private ListView listView_plate;
    @ViewInject(R.id.text_pQ)
    private TextView text_planQuantity;
    @ViewInject(R.id.text_aQ)
    private TextView text_actualQuantity;
    @ViewInject(R.id.text_fileName)
    private TextView text_fileName;
    @ViewInject(R.id.text_materialNo)
    private TextView text_materialNo;
    @ViewInject(R.id.text_type)
    private TextView text_type;
    @ViewInject(R.id.tv_no_robot)
    private TextView tv_no_robot;
    @ViewInject(R.id.tv_isrunning)
    private TextView tv_isrunning;
    @ViewInject(R.id.btn_ok)
    private Button btn_ok;
    private TaskInfo taskInfo;
    private ArrayList<MaterialPlateInfo> materialPlateInfos = new ArrayList<>();
    private String materialId;
    private int quantity;
    private String no;

    private TaskInfo parkingTaskInfos = null;
    // 还未完成的任务条目的位置
    private ArrayList<Integer> positions = new ArrayList<>();
    private String TAG = "FinishTaskItemActivity";

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 1000);// 间隔120秒
        }
        void update() {
            GetWindowParkingTaskItem getWindowParkingTaskItem = new GetWindowParkingTaskItem();
            getWindowParkingTaskItem.setHandler(new Handler(){
                public void handleMessage(Message msg) {
                    parkingTaskInfos = (TaskInfo) msg.obj;
                    if(parkingTaskInfos !=null){
                        //Toast.makeText(getActivity(),"叉车已到站，请上料",Toast.LENGTH_SHORT).show();
                        MyApplication.setParkingTaskInfo(parkingTaskInfos );
                        btn_ok.setClickable(true);
                        btn_ok.setVisibility(View.VISIBLE);
                        tv_no_robot.setVisibility(View.GONE);
                        refleshData(parkingTaskInfos);
                    }else{
                        MyApplication.setParkingTaskInfo(null );
                        initData();
                        btn_ok.setVisibility(View.GONE);
                        tv_no_robot.setVisibility(View.VISIBLE);
                        tv_no_robot.bringToFront();
                    }
                }
            });
            getWindowParkingTaskItem.startGetWindowParkingTaskItemThread();
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        tv_isrunning.setText("仓口"+MyApplication.getWindowId()+"-到站物料");
        // 扫料盘
        edit_plateId.addTextChangedListener(new MyTextWatcher(edit_plateId));
        edit_plateId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER)) {
                    switch (keyEvent.getAction()) {
                        //按下
                        case KeyEvent.ACTION_DOWN:
                            //扫描内容
                            String strValue = String.valueOf(((EditText) textView).getText());
                            edit_plateId.setText(strValue.replace("/r", "").replace("\n", ""));
                            strValue = strValue.trim();
                            switch (textView.getId()) {
                                case R.id.edit_plateId:
                                    if (countStr(strValue,'@') == 8) {
                                        parePlateInfo(strValue);
                                        edit_plateId.setText(no);
                                        strValue = String.valueOf(((EditText) textView).getText()).trim();
                                        Log.d("strValue2", strValue);
                                        // 判断扫描的料号信息和任务条目任务信息是否相同
                                        taskInfo = MyApplication.getParkingTaskInfo();
                                        if (taskInfo == null){
                                            edit_plateId.setText("");
                                            return false;
                                        }
                                        if (!(strValue).equals(taskInfo.getMaterialNo())) {
                                            edit_plateId.setText("");
                                            Toast.makeText(getActivity(), "料盘扫描错误", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "料盘扫描正确", Toast.LENGTH_SHORT).show();
                                            new WriteLogEvent().writeLog(taskInfo, materialId, quantity, no);
                                        }

                                    }else {
                                        edit_plateId.setText("");
                                        Toast.makeText(getActivity(), "料盘扫描错误", Toast.LENGTH_SHORT).show();
                                    }
                            }
                            break;
                    }
                }
                return false;
            }
        });
        handler.postDelayed(runnable, 0);

    }

    /**
     * 初始化数据（可不要？）
     */
    public void initData(){
        edit_plateId.setText("");
        text_planQuantity.setText("0");
        text_actualQuantity.setText("0");
        text_fileName.setText("");
        text_type.setText("");
        text_materialNo.setText("");
        materialPlateInfos = new ArrayList<>();
        MaterialPlateListViewAdapter materialPlateListViewAdapter = new MaterialPlateListViewAdapter(MyApplication.getContext(), R.layout.material_plate_item,materialPlateInfos);
        listView_plate.setAdapter(materialPlateListViewAdapter);
    }

    /**
     * 根据输入的TaskInfo刷新界面
     * @param taskInfo
     */
    public  void refleshData(TaskInfo taskInfo){
        MyApplication.setParkingTaskInfo(taskInfo);
        text_planQuantity.setText(taskInfo.getPlanQuantity()+"");
        text_actualQuantity.setText(taskInfo.getActualQuantity()+"");
        text_fileName.setText(taskInfo.getFileName());
        text_type.setText(taskInfo.getType());
        text_materialNo.setText(taskInfo.getMaterialNo());
        materialPlateInfos = taskInfo.getMaterialPlateInfos();
        MaterialPlateListViewAdapter materialPlateListViewAdapter = new MaterialPlateListViewAdapter(MyApplication.getContext(), R.layout.material_plate_item,materialPlateInfos);
        listView_plate.setAdapter(materialPlateListViewAdapter);
    }

    /**
     * ”操作完毕“按钮点击事件
     * @param view
     */
    @Event(value={R.id.btn_ok})
    private void btn_finishItem(View view){
        showExitDialog();
    }

    /**
     * 提示操作是否完毕
     */
    private void showExitDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("你确定操作完毕了吗?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                            taskInfo = MyApplication.getParkingTaskInfo();
                            new RobotBackEvent().robotBack(taskInfo.getTaskId());
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    /* *
     * 提示所有任务是否完成
     */
    /*private void showIsFinishDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("当前该仓库任务条目已完成，是否返回前一个页面")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(getActivity(), GetWindowTaskItemActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }*/

    /* *
     * 扫料盘信息解析
     * @param str
     */
    private void parePlateInfo(String str){
        String[] a = str.split("@");
        no = a[0];
        quantity = Integer.parseInt(a[1]);
        materialId = a[2];
        Log.d("no",no);
        Log.d("materialId",materialId);
        Log.d("quantity",quantity+"");
    }
    /**
     * 判断扫面二维码含有多少个特定字符
     * @param str  字符串
     * @param c 字符
     */
    private int countStr(String str,char c){
        int num = 0;
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++)
        {
            if(c == chars[i])
            {
                num++;
            }
        }
        return num;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
