package com.jimi.uw_server.model.vo;

import com.jimi.uw_server.model.MaterialType;

public class MaterialTypeVO extends MaterialType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557628227809950920L;

	private String enabledString;
	
	private Integer quantity;
	
	private Integer totalRow;
	
	private Integer pageNumber;
	
	private boolean firstPage;
	
	private boolean lastPage;
	
	private Integer totalPage;
	
	private Integer pageSize;
	
//	private static final String countSelectSql = "SELECT material_type.*, SUM(material.remainder_quantity) AS quantity"
//			+ " FROM material_type,material WHERE material_type.id=material.type AND material_type.enabled=1"
//			+ " group by material_type.id";

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Integer getQuantity() {
		return quantity;
	}

	public void setEnabledString(String enabledString) {
		this.enabledString = enabledString;
	}
	
	public String getEnabledString() {
		return enabledString;
	}

	public Integer getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(Integer totalRow) {
		this.totalRow = totalRow;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public boolean isFirstPage() {
		return firstPage;
	}

	public void setFirstPage(boolean firstPage) {
		this.firstPage = firstPage;
	}

	public boolean isLastPage() {
		return lastPage;
	}

	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public static void setPaginate(Integer totalRow, Integer pageNumber, Integer lastPage, Integer firstPage, Integer totalPage, Integer pageSize) {
		
	}
	
//	public MaterialTypeVO(Integer id, String no, Integer area, Integer row, Integer col, Integer height, boolean enabled, 
//			boolean is_on_shelf, Integer quantity) {
//		this.setId(id);
//		this.setNo(no);
//		this.setArea(area);
//		this.setRow(row);
//		this.setCol(col);
//		this.setHeight(height);
//		this.setEnabled(enabled);
//		this.setEnabledString(enabled ? "是" : "否");
//		this.setIsOnShelf(is_on_shelf);
//		this.setQuantity(quantity);
//	}

}
