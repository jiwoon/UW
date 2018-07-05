package com.jimi.uw_server.model.vo;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:25:16 
 */
public class MaterialTypeVO {
	
	private Integer id;
	
	private String no;
	
	private Integer area;
	
	private Integer row;
	
	private Integer col;
	
	private Integer height;
	
	private boolean enabled;

	private String enabledString;
	
	private Integer quantity;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public Integer getArea() {
		return area;
	}

	public void setArea(Integer area) {
		this.area = area;
	}
	
	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public Integer getCol() {
		return col;
	}

	public void setCol(Integer col) {
		this.col = col;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabledString(String enabledString) {
		this.enabledString = enabledString;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getQuantity() {
		return quantity;
	}
	
	public String getEnabledString() {
		if (this.isEnabled()) {
			this.enabledString = "是";
		} else {
			this.enabledString = "否";
		}
		return enabledString;
	}
	
	public MaterialTypeVO(Integer id, String no, Integer area, Integer row, Integer col, Integer height, 
			boolean enabled, Integer quantity) {
		this.id = id;
		this.no = no;
		this.area = area;
		this.row = row;
		this.col = col;
		this.height = height;
		this.enabled = enabled;
		this.quantity = quantity;
	}

}
 