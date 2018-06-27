package com.jimi.uw_server.util;

import java.util.Date;

import com.jimi.uw_server.model.ErrorLog;

public class ErrorLogWritter {
	
	public static boolean saveErrorLog(String message) {
		ErrorLog errorLog = new ErrorLog();
		if (errorLog.set("message", message).set("time", new Date()).save()) {
			System.out.println("record: " + errorLog);
			return true;
		}
		return false;
	}

}
