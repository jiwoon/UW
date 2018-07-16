package com.jimi.agv_mock.handle;

import com.alibaba.fastjson.JSON;
import com.jimi.agv_mock.entity.cmd.AGVMoveCmd;
import com.jimi.agv_mock.thread.TaskExcutor;

/**
 * LSSL处理器
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class LSSLHandler {

	
	public static void handleLSSL(String message) {
		//转换成实体类
		AGVMoveCmd moveCmd = JSON.parseObject(message, AGVMoveCmd.class);
		//放入任务池
		//...
	}

}
