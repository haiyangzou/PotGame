package org.pot.remote.thrift.define.chat;

import org.pot.common.communication.server.ServerType;
import org.pot.message.protocol.chat.*;
import org.pot.remote.thrift.define.IRemote;
import org.pot.remote.thrift.define.RemoteServerType;

@RemoteServerType(ServerType.CHAT_SERVER)
public interface ChatRemote extends IRemote {
    ChatListS2C getChatList(ChatListS2S message);

    ChatPersonalListS2C getChatPersonalList(ChatPersonalListS2S request);

    PushChatS2C sendChat(ChatS2S request);

    void chatPersonalRead(ChatPersonalReadS2S request);

    PushPersonalChatS2C sendPersonalChat(ChatPersonalS2S request);
}
