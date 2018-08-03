package com.example.jimi.in_outstock.entity;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 任务条目信息
 */
public class TaskInfo implements Serializable{
    // 任务ID
    private int taskId;
    // 计划数量
    private int  planQuantity;
    // 实际数量
    private int actualQuantity;
    //类型
    private String type;
    // 料盘扫描信息
    private ArrayList<MaterialPlateInfo> materialPlateInfos;
    // 完成时间
    private String finishTime;
    // 任务
    private String fileName;
    // 料号
    private String materialNo;

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    public void setType(String type){ this.type = type;}

    public String getType(){return this.type;}

    public void setPlanQuantity(int planQuantity) {
        this.planQuantity = planQuantity;
    }

    public int getPlanQuantity() {
        return planQuantity;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public ArrayList<MaterialPlateInfo> getMaterialPlateInfos() {
        return materialPlateInfos;
    }

    public void setMaterialPlateInfos(ArrayList<MaterialPlateInfo> materialPlateInfos) {
        this.materialPlateInfos = materialPlateInfos;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getMaterialNo() {
        return materialNo;
    }
}
