package script.msg


import lombok.extern.slf4j.Slf4j
import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script
import org.pot.login.domain.object.AccountsData
import org.pot.login.service.AccountService

import javax.annotation.Resource

/**
 *
 * @author zhy* @Data Create in 2017/12/20
 */
@Script(type = 1, name = "", path = "/login_auth")
@Slf4j
class LoginAuth extends HttpHandler {
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
            String token ="d"
            Long openId = getParam().get("openId")

            System.out.println();
            //todo send pb to game server
            AccountsData account = accountService.defaultInstance(openId)
            String error = account.status == 1?"":""

        } finally {
            response()
        }
    }
}
