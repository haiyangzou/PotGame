package org.pot.login.jobs;

import lombok.extern.slf4j.Slf4j;
import org.pot.login.beans.ServerConst;
import org.pot.login.service.ServerListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ServerListJob {
    @Resource
    private ServerConst serverConst;
    @Resource
    private ServerListService serverListService;
}
