package com.jimi.agv_mock.disturber;

import java.util.Random;

import com.jimi.agv_mock.constant.Constant;
import com.jimi.agv_mock.socket.MockMainSocket;

/**
 * ACK干扰器
 * <br>
 * <b>2018年7月20日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class ACKDisturber {

	public static void disturbe(String message) {
		if(Constant.DISTURB_SWITCH) {
			//模拟丢失
			int a = Math.abs(new Random().nextInt() % 100);
			if(a >= Constant.DONT_SEND_ACK_PERCENTAGE) {
				//模拟延迟
				long delay = (long) (Constant.SEND_ACK_DELAY * (1 + ((new Random().nextInt() % (Constant.ACK_FLOATING_PERCENTAGE + 1)) / 100.0)));
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				MockMainSocket.sendACK(message);
			}
		}else {
			MockMainSocket.sendACK(message);
		}
		
	}
	
}
