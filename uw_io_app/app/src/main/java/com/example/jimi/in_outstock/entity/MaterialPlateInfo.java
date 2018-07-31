package com.example.jimi.in_outstock.entity;

/**
 * 料盘信息
 */
public class MaterialPlateInfo {
    // 料盘时间戳
    private String materialId;
    // 物料数量
    private int quantity;

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
