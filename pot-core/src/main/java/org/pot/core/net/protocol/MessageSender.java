package org.pot.core.net.protocol;

import com.google.protobuf.Message;

public interface MessageSender {
    void sendMessage(Message message);
}
