package com.jimi.agv_mock.entity.cmd.base;

/**
 * AGV指令父类
 * <br>
 * <b>2018年6月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class AGVBaseCmd {

	protected String cmdcode;
	
	protected Integer cmdid;
	
	public String getCmdcode() {
		return cmdcode;
	}
	public void setCmdcode(String cmdcode) {
		this.cmdcode = cmdcode;
	}
	public Integer getCmdid() {
		return cmdid;
	}
	public void setCmdid(Integer cmdid) {
		this.cmdid = cmdid;
	}
	
}
