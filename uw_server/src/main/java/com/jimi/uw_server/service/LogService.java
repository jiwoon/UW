package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.exception.ParameterException;
import com.jimi.uw_server.model.vo.PositionLogVO;
import com.jimi.uw_server.model.vo.TaskLogVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;

/**
 * 日志业务层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class LogService extends SelectService {

	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	
	public Object selectLog(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		if (table.equals("action_log")) {
			return selectService.select(table, pageNo, pageSize, ascBy, descBy, filter);
		} else if(table.equals("task_log")) {
			
			if (filter != null) {
				if (filter.contains("taskId")) {
					filter = filter.replace("taskId", "task_id");
				}
				if (filter.contains("taskType")) {
					filter = filter.replace("taskType", "task_type");
				}
			}
			
			List<TaskLogVO> taskLogVO = new ArrayList<TaskLogVO>();
			Page<Record> result = selectService.select("task_log", pageNo, pageSize, ascBy, descBy, filter);
			
			int totallyRow =  result.getTotalRow();
			for (Record res : result.getList()) {
				TaskLogVO t = new TaskLogVO(res.get("id"), res.get("task_id"), res.get("material_id"), 
						res.get("quantity"), res.get("operator"), res.get("auto"), res.get("time"));
				taskLogVO.add(t);
			}
			PagePaginate pagePaginate = new PagePaginate();
			pagePaginate.setPageSize(pageSize);
			pagePaginate.setPageNumber(pageNo);
			pagePaginate.setTotalRow(totallyRow);
			
			pagePaginate.setList(taskLogVO);

			return pagePaginate;
			
		} else if(table.equals("position_log")){
			
			if (filter != null) {
				if (filter.contains("materialNo")) {
					filter = filter.replace("materialNo", "no");
				}
				if (filter.contains("taskType")) {
					filter = filter.replace("taskType", "task_type");
				}
			}
			
			List<PositionLogVO> positionLogVO = new ArrayList<PositionLogVO>();
			
			if (selectService.select("position_log", pageNo, pageSize, ascBy, descBy, filter) == null) {
				throw new ParameterException("参数错误！");
			}
			Page<Record> result = selectService.select("position_log", pageNo, pageSize, ascBy, descBy, filter);
			
			int totallyRow =  result.getTotalRow();
			for (Record res : result.getList()) {
				PositionLogVO p = new PositionLogVO(res.get("id"), res.get("task_id"), res.get("material_id"), res.get("old_area"), 
						res.get("old_row"), res.get("old_col"), res.get("old_height"), res.get("new_area"), 
						res.get("new_row"), res.get("new_col"), res.get("new_height"), res.get("time"));
				positionLogVO.add(p);
			}
			PagePaginate pagePaginate = new PagePaginate();
			pagePaginate.setPageSize(pageSize);
			pagePaginate.setPageNumber(pageNo);
			pagePaginate.setTotalRow(totallyRow);
			
			pagePaginate.setList(positionLogVO);

			return pagePaginate;
			
		} else {
			throw new OperationException("请输入正确的日志表名！");
		}
	}
	
}
