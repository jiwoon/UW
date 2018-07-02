package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.MaterialType;

public class MaterialTypeVO extends MaterialType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557628227809950920L;

	private Integer id;

	private String no;
	
	private Integer area;
	
	private Integer row;
	
	private Integer col;
	
	private Integer height;
	
	private Boolean enabled;
	
	private String enabledString;
	
	private static final String countSelectSql = "SELECT material_type.*, SUM(material.remainder_quantity) AS quantity"
			+ " FROM material_type,material WHERE material_type.id=material.type AND material_type.enabled=1"
			+ " group by material_type.id";
	
	private Integer quantity;

	public Integer getId() {
		return id;
	}

	public MaterialType setId(Integer id) {
		 this.id = id;
		 return (MaterialType)this;
	}

	public String getNo() {
		return no;
	}

	public MaterialType setNo(String no) {
		this.no = no;
		return (MaterialType)this;
	}

	public Integer getArea() {
		return area;
	}

	public MaterialType setArea(Integer area) {
		this.area = area;
		return (MaterialType)this;
	}

	public Integer getRow() {
		return row;
	}

	public MaterialType setRow(Integer row) {
		this.row = row;
		return (MaterialType)this;
	}

	public Integer getCol() {
		return col;
	}

	public MaterialType setCol(Integer col) {
		this.col = col;
		return (MaterialType)this;
	}

	public Integer getHeight() {
		return height;
	}

	public MaterialType setHeight(Integer height) {
		this.height = height;
		return (MaterialType)this;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public MaterialType setEnabled(boolean enabled) {
		this.enabled = enabled;
		return (MaterialType)this;
	}

	public String getEnabledString() {
		return enabledString;
	}

	public void setEnabledString(String enabledString) {
		if(this.getEnabled()) {
			this.enabledString = "是";
		} else {
			this.enabledString = "否";
		}
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
}
