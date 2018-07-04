package com.jimi.uw_server.service.entity;

public class Page {
	
	private Integer firstIndex;
	private Integer currentPage;
	private Integer pageSize;
	private Integer totallyPage;
	private Integer totallyData;

	public Integer getFirstIndex() {
		firstIndex = ((this.getCurrentPage()-1)*this.getPageSize());
		return firstIndex;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		if(currentPage == null || currentPage <= 0) {
			this.currentPage = 1;
		}else {
			this.currentPage = currentPage;
		}
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotallyPage() {
		return totallyPage;
	}
	public void setTotallyPage(Integer totallyPage) {
		this.totallyPage = totallyPage;
		if (this.totallyPage<this.currentPage) {
			this.setCurrentPage(totallyPage);
		}
	}
	public Integer getTotallyData() {
		return totallyData;
	}
	public void setTotallyData(Integer totallyData) {
		this.totallyData = totallyData;
		if(this.totallyData == null && this.totallyData <= 0) {
			this.totallyPage = 1;
		}else {
			this.setTotallyPage(this.totallyData / this.getPageSize() 
					+ (this.totallyData % this.getPageSize() == 0 ? 0: 1));
		}
	}
	
}
