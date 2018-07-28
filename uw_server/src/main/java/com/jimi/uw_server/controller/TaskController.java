package com.jimi.uw_server.controller;

import java.io.File;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jfinal.upload.UploadFile;
import com.jimi.uw_server.model.PackingListItem;
import com.jimi.uw_server.model.Task;
import com.jimi.uw_server.model.User;
import com.jimi.uw_server.model.Window;
import com.jimi.uw_server.service.TaskService;
import com.jimi.uw_server.util.ResultUtil;
import com.jimi.uw_server.util.TokenBox;

/**
 * 任务控制层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class TaskController extends Controller {

	private static TaskService taskService = Enhancer.enhance(TaskService.class);

	public static final String SESSION_KEY_LOGIN_USER = "loginUser";


	// 创建任务
//	@Log("开始了任务{task}")
	public void create(@Para("") Task task, @Para("") PackingListItem packingListItem, UploadFile file, Integer type) throws Exception {
		// 如果是创建「出入库任务」，入库type为0，出库type为1
		if (type.equals(0) || type.equals(1)) {
			file = getFile();
			String fileName = file.getFileName();
			String fullFileName = file.getUploadPath() + File.separator + file.getFileName();
			if(taskService.createIOTask(task, type, fileName, fullFileName)) {
				if (taskService.insertPackingList(packingListItem, type, fullFileName)) {
					renderJson(ResultUtil.succeed());
				} else {
					renderJson(ResultUtil.failed("插入套料单失败，该任务已作废，请确保套料单中所有料号都在物料类型表中有对应记录再进行导入！"));
				}
			} else {
				renderJson(ResultUtil.failed("创建任务失败，请检查套料单的文件格式及内容格式！"));
			}
		} else if (type.equals(2) ) {	//如果是创建「盘点任务」
			renderJson(ResultUtil.failed("该功能尚在开发中！"));
		} else if (type.equals(3)) {	//如果是创建「位置优化任务」
			renderJson(ResultUtil.failed("该功能尚在开发中！"));
		}
		
	}


	// 令指定任务通过审核
//	@Log("审核通过了任务编号为{id}的任务")
	public void pass(Integer id) {
		if(taskService.pass(id)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed("审核失败！"));
		}
	}


	// 令指定任务开始
//	@Log("开始了任务编号为{id}的任务")
	public void start(Integer id, Integer window) {
		if(taskService.start(id, window)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
	}
	

	// 作废指定任务
//	@Log("作废了任务编号为{id}的任务")
	public void cancel(Integer id) {
		if(taskService.cancel(id)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed("作废失败！"));
		}
	}


	// 查看任务详情
	public void check(Integer id, Integer pageSize, Integer pageNo) {
		renderJson(ResultUtil.succeed(taskService.check(id, pageSize, pageNo)));
	}
	

	// 查询所有仓口
	public void getWindows(@Para("") Window window) {
		renderJson(ResultUtil.succeed(taskService.getWindows(window)));
	}


	// 查询所有任务
	public void select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		renderJson(ResultUtil.succeed(taskService.select(pageNo, pageSize, ascBy, descBy, filter)));
	}


	// 获取指定仓口任务条目
	public void getWindowTaskItems(Integer id, Integer pageNo, Integer pageSize) {
		renderJson(ResultUtil.succeed(taskService.getWindowTaskItems(id, pageNo, pageSize)));
	}


	// 物料出入库
	public void io(Integer packListItemId, String materialId, Integer quantity, String no) {
		// 获取当前使用系统的用户，以便获取操作员uid
		String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
		User user = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
		if (taskService.io(packListItemId, materialId, quantity, no, user)) {
			renderJson(ResultUtil.succeed());
		} else {
			renderJson(ResultUtil.failed());
		}
		
	}

	
}
