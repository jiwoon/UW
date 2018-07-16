package com.jimi.agv_mock.entity.cmd;

import java.util.List;

import com.jimi.agv_mock.entity.cmd.base.AGVBaseCmd;

/**
 * 机器启用禁用指令
 * <br>
 * <b>2018年7月13日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVSwitchEnableCmd extends AGVBaseCmd{

	private List<Integer> robotids;

	public List<Integer> getRobotids() {
		return robotids;
	}

	public void setRobotids(List<Integer> robotids) {
		this.robotids = robotids;
	}
}
