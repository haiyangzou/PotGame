package script.msg


import lombok.extern.slf4j.Slf4j
import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script
import org.pot.core.service.KafkaProducerService
import org.pot.login.service.AccountService
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.Resource

/**
 *
 * @author zhy* @Data Create in 2017/12/20
 */
@Script(type = 1, name = "", path = "/favicon.ico")
@Slf4j
class GetFavicon extends HttpHandler {
    @Override
    void run() {
        String result = "favicon.ico";
        setMsgBytes(result.getBytes());

    }
}
