package script.msg


import lombok.extern.slf4j.Slf4j
import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script
import org.pot.core.service.KafkaProducerService
import org.springframework.beans.factory.annotation.Autowired

/**
 *
 * @author zhy* @Data Create in 2017/12/20
 */
@Script(type = 1, name = "", path = "/login_auth")
@Slf4j
class TestMsgScript extends HttpHandler {
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Override
    void run() {
        try {

            //todo send pb to game server
//            WorldProtocol.MarchRequest request = WorldProtocol.MarchRequest.newBuilder()
//                    .setArmyIndex(1)
//                    .setIsSituStation(false)
//                    .setMarchType(WorldProtocol.MarchType.ATTACK)
//                    .setMarchTypeValue(1)
//                    .build();
//            kafkaProducerService.sendPb(request);
//            setMsgBytes(var.getBytes());
        } finally {
            response()
        }
    }
}
