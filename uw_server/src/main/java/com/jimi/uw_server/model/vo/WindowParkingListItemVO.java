package com.jimi.uw_server.model.vo;

import java.util.List;
import com.jimi.uw_server.model.TaskLog;

/**
 * 
 * @author HardyYao
 * @createTime 2018年7月23日 下午4:04:25
 */
public class WindowParkingListItemVO extends TaskLog {

	private static final long serialVersionUID = -2631121149118866618L;
	
	private List<?> details;


	public String getType(Integer type) {
		String typeString = "入库";
		if (type == 0) {
			typeString = "入库";
		} else if (type == 1) {
			typeString = "出库";
		} else if (type == 2) {
			typeString = "盘点";
		}  else if (type == 3) {
			typeString = "位置优化";
		}
		return typeString;
	}
	
	public String getMaterialNo(String materialNo) {
		return materialNo;
	}

	public WindowParkingListItemVO(Integer packingListItemId, String fileName, Integer type, String materialNo, Integer planQuantity, Integer actualQuantity) {
		this.set("id", packingListItemId);
		this.set("fileName", fileName);
		this.set("type", getType(type));
		this.set("materialNo", materialNo);
		this.set("planQuantity", planQuantity);
		this.set("actualQuantity", actualQuantity);
	}

	public List<?> getDetails() {
		return details;
	}

	public void setDetails(List<?> details) {
		this.set("details", details);
	}

}
