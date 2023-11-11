package script.msg


import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script

import javax.annotation.Resource

/**
 *
 * @author zhy* @Data Create in 2017/12/20
 */
@Script(type = 1, name = "", path = "/get_room")
@groovy.util.logging.Slf4j
class GetRoom extends HttpHandler {
    @Resource
    RoomService roomService;
    @Override
    void run() {
        try {
            log.info("room:{}",roomService.getRoom())
        } finally {
            response()
        }
    }
}
