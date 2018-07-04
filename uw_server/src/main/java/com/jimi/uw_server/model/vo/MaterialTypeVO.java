package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.MaterialType;

public class MaterialTypeVO extends MaterialType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557628227809950920L;

	private String enabledString;
	
	private Integer quantity;
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getQuantity() {
		return quantity;
	}
	
	public String getEnabledString() {
		if (this.getEnabled()) {
			this.enabledString = "是";
		} else {
			this.enabledString = "否";
		}
		return enabledString;
	}
	
	public MaterialTypeVO(Integer id, String no, Integer area, Integer row, Integer col, Integer height, 
			boolean enabled, boolean isOnShelf, Integer quantity) {
		this.setId(id);
		this.setNo(no);
		this.setArea(area);
		this.setRow(row);
		this.setCol(col);
		this.setHeight(height);
		this.setEnabled(enabled);
		this.setIsOnShelf(isOnShelf);
		this.setQuantity(quantity);
	}
	
}
