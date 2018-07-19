package com.jimi.uw_server.service.entity;

import java.util.List;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:26:39 
 */
public class PagePaginate {
	
	private Integer pageNumber;
	
	private Integer pageSize;
	
	private boolean firstPage;
	
	private boolean lastPage;
	
	private Integer totalPage;
	
	private Integer totalRow;
	
	private List<?> list;

	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		if(pageNumber == null || pageNumber <= 0) {
			this.pageNumber = 1;
		}else {
			this.pageNumber = pageNumber;
		}
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		if(pageSize == null || pageSize <= 0) {
			this.pageSize = 20;
		}else {
			this.pageSize = pageSize;
		}
	}
	public boolean isFirstPage() {
		if(this.getPageNumber() == 1) {
			this.firstPage = true;
		} else {
			this.firstPage = false;
		}
		return firstPage;
	}
	public boolean isLastPage() {
		if(this.getPageNumber() == totalPage) {
			this.lastPage = true;
		} else {
			this.lastPage = false;
		}
		return lastPage;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
		if (this.totalPage<this.pageNumber) {
			this.setPageNumber(totalPage);
		}
	}
	public Integer getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(Integer totallyData) {
		this.totalRow = totallyData;
		if(this.totalRow == null && this.totalRow <= 0) {
			this.totalPage = 1;
		}else {
			this.setTotalPage(this.totalRow / this.getPageSize() 
					+ (this.totalRow % this.getPageSize() == 0 ? 0: 1));
		}
	}
	public List<?> getList() {
		return list;
	}
	public void setList(List<?> entitiesVO) {
		this.list = entitiesVO;
	}
}
 