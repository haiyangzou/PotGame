package script.msg


import lombok.extern.slf4j.Slf4j
import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script
import org.pot.core.util.TimeUtil
import org.pot.login.domain.object.AccountsData
import org.pot.login.service.AccountService

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
            AccountsData account = accountService.defaultInstance(openId)
            if(Objects.isNull(account)){
                account.setCreated_at(TimeUtil.currentTimeMillis())
                account.setStatus(1)
                account.setOpenid(openId)
                account.setUpdated_at(TimeUtil.currentTimeMillis())
                log.info("account not find")
            }

            log.info("content:{}",openId)
        } finally {
            response()
        }
    }
}
