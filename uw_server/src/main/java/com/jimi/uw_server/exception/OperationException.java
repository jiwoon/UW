package com.jimi.uw_server.exception;

/**
 * 操作失败异常，result：412
 * <br>
 * <b>2018年6月2日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class OperationException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OperationException(String message) {
		super(message);
	}
	
}
