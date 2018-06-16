package com.jimi.uw_server.service;

import com.jimi.uw_server.model.Robot;

public class RobotService {
	
	public boolean robotSwitch(Integer id, Integer enabled) {
		Robot robot = new Robot();
		robot.setId(id);
		robot.setEnabled(enabled);
		robot.keep("id", "status", "battery", "x", "y", "enabled");
		return robot.update();
	}

}
