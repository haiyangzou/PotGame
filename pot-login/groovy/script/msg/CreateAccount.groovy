package script.msg


import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script
import org.pot.core.util.TimeUtil
import org.pot.login.domain.object.UserAccount

import javax.annotation.Resource

/**
 *
 * @author zhy* @Data Create in 2017/12/20
 */
@Script(type = 1, name = "", path = "/create_account")
@groovy.util.logging.Slf4j
class CreateAccount extends HttpHandler {
//    @Autowired
//    private KafkaProducerService kafkaProducerService;
    @Resource
    AccountService accountService;

    @Override
    void run() {
        try {
            if (!getParam().containsKey("openId")) {
                setMsgBytes()
                return
            }
            Long openId = Long.parseLong(getParam().get("openId"))
            UserAccount account = accountService.defaultInstance(openId)
            account.setCreated_at(TimeUtil.currentTimeMillis())
            account.setStatus(1)
            account.setOpenid(openId)
            account.setUpdated_at(TimeUtil.currentTimeMillis())
            accountService.save(openId,account)
            log.info("content:{}", openId)
        } finally {
            response()
        }
    }
}
