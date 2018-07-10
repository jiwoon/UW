package com.jimi.uw_server.model.bo;

import com.jimi.uw_server.util.ExcelHelper.Excel;

/**
 * @author HardyYao
 * @createTime 2018年7月5日 上午11:23:18 
 */
public class PackingListItemBO {
	
	@Excel(col=1, head="料号")
	private String no;
	@Excel(col=6, head="需求数")
	private Integer quantity;

	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
 