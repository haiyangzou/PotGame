package org.pot.room.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * shutdown线程
 *
 * @author zhy
 */
@Component
public class ShutdownThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownThread.class);

    @Autowired
    private ZKService ZKService;

    public ShutdownThread() {
    }

    @Override
    public void run() {
        try {
            ZKService.destroy();
            logger.info("login server begin shutdown...");
            logger.info(" loginserver shutdown!");

        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
