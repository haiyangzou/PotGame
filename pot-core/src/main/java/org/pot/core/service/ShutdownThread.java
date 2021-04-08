package org.pot.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * shutdown线程
 *
 * @author zhy
 */
@Component
public class ShutdownThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(ShutdownThread.class);


	public ShutdownThread() {
	}

	@Override
	public void run() {
		try {
			logger.info("server begin shutdown...");
			logger.info("server shutdown!");
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
