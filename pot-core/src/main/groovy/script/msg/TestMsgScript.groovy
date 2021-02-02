package script.msg

import io.netty.buffer.ByteBuf
import lombok.extern.slf4j.Slf4j
import org.pot.core.io.handler.HttpHandler
import org.pot.core.script.Script

/**
 *
 * @author zhy* @Data Create in 2017/12/20
 */
@Script(type = 1, name = "", path = "/gatelist")
@Slf4j
class TestMsgScript extends HttpHandler {
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
            setMsgBytes(var.getBytes());
        } finally {
            response()
        }
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
