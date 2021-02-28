package script.msg

import io.netty.buffer.ByteBuf
import lombok.extern.slf4j.Slf4j
import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script
import org.pot.core.service.KafkaProducerService
import org.springframework.beans.factory.annotation.Autowired

/**
 *
 * @author zhy* @Data Create in 2017/12/20
 */
@Script(type = 1, name = "", path = "/gatelist")
@Slf4j
class TestMsgScript extends HttpHandler {
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Override
    void run() {
        try {
            String list = "123"
            String list2 = System.currentTimeMillis()
            ByteBuf buf = message.content()
            if (buf.hasArray()) {
                list2 = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
            }
            def var = """hello
            world
            groovy~"""
            repeat(var)
            println var
            //todo send pb to game server
//            WorldProtocol.MarchRequest request = WorldProtocol.MarchRequest.newBuilder()
//                    .setArmyIndex(1)
//                    .setIsSituStation(false)
//                    .setMarchType(WorldProtocol.MarchType.ATTACK)
//                    .setMarchTypeValue(1)
//                    .build();
//            kafkaProducerService.sendPb(request);
            setMsgBytes(var.getBytes());
        } finally {
            response()
        }
    }
    def sendMessage(){

    }
    def repeat(val) {
        for (int i = 0; i < 5; i++) {
            println val
        }
    }

    private void test() {
        System.out.println("test")
    }
}
