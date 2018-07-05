package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.vo.TaskLogVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;

public class LogService extends SelectService {

	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	
	public Object selectLog(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		if (table.equals("action_log")) {
			return selectService.select(table, pageNo, pageSize, ascBy, descBy, filter);
		} else if(table.equals("task_log")) {
			
			List<TaskLogVO> taskLogVO = new ArrayList<TaskLogVO>();
			Page<Record> result = selectService.select("task_log", pageNo, pageSize, ascBy, descBy, filter);
			
			int totallyRow =  0;
			for (Record res : result.getList()) {
				TaskLogVO t = new TaskLogVO(res.get("id"), res.get("task_id"), res.get("task_type"), res.get("material_id"), res.get("material_no"),
						res.get("quantity"), res.get("operator"), res.get("auto"), res.get("time"));
				taskLogVO.add(t);
				totallyRow++;
			}
			PagePaginate pagePaginate = new PagePaginate();
			pagePaginate.setPageSize(pageSize);
			pagePaginate.setPageNumber(pageNo);
			pagePaginate.setTotalRow(totallyRow);
			
			pagePaginate.setList(taskLogVO);

			return pagePaginate;
			
		} else if(table.equals("postion_log")){
			
			
			
		} else {
			throw new OperationException("请输入正确的日志表名！");
		}
		return null;
	}
	
}
