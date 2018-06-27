package com.jimi.uw_server.model.bo;

import com.jimi.uw_server.util.ExcelHelper.Excel;

public class PackingListItemBO {
	
	@Excel(col=1, head="料号")
	private String no;
	@Excel(col=6, head="需求数")
	private String quantity;

	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

}
