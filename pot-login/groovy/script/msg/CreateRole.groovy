package script.msg


import lombok.extern.slf4j.Slf4j
import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script
import org.pot.core.util.TimeUtil
import org.pot.login.domain.object.UserAccount

import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.Resource

/**
 *
 * @author zhy* @Data Create in 2017/12/20
 */
@Script(type = 1, name = "", path = "/create_role")
@groovy.util.logging.Slf4j
class CreateRole extends HttpHandler {
//    @Autowired
//    private KafkaProducerService kafkaProducerService;
    @Resource
    AccountService accountService;
    @Override
    void run() {
        try {
            if(!getParam().containsKey("openId")){
                setMsgBytes()
                return
            }
            String token ="db"
            Long openId = Long.parseLong(getParam().get("openId"))
            log.info("content:{}",openId)
        } finally {
            response()
        }
    }
}
