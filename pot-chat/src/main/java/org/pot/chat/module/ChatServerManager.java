package org.pot.chat.module;

import com.eyu.kylin.magics.protocol.chat.ChatPersonalS2S;
import com.google.protobuf.Message;
import org.pot.chat.engine.ChatEngine;
import org.pot.common.communication.server.ServerId;
import org.pot.common.communication.server.ServerType;
import org.pot.common.util.JsonUtil;
import org.pot.core.enums.ChatChannel;
import org.pot.dal.redis.ReactiveRedis;
import org.pot.remote.thrift.define.chat.ChatRemote;
import org.pot.remote.thrift.client.manager.RpcClientManager;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.util.stream.Collectors;

public class ChatServerManager {
    private static final int DEFAULT_SIZE = 20;

    private static final int COUNTRY_MAX_SIZE = 1000;

    private static final long UNION_EXPIRE_TIME = TimeUnit.DAYS.toMillis(14L);

    private static final int DEFAULT_MAX_SIZE = 1000;

    private ReactiveStringRedisTemplate CHAT;

    public void init() {
        this.CHAT = ReactiveRedis.createReactiveStringRedisTemplate((ChatEngine.getInstance().getConfig()).getChatRedisConfig());
    }

    public void personalRead(long playerId, long otherId) {
        String chatPersonalRedisKey = getChatPersonalRedisKey(playerId, otherId);
        List<Map.Entry<Object, Object>> block = this.CHAT.opsForHash().entries(chatPersonalRedisKey).collectList().block();
        if (block != null)
            block.forEach(entry -> {
                ChatPersonal chatPersonal = JsonUtil.parseJson(entry.getValue().toString(), ChatPersonal.class);
                if (chatPersonal.getSenderPlayerId() == otherId && !chatPersonal.isStatus()) {
                    chatPersonal.setStatus(true);
                    this.CHAT.opsForHash().put(chatPersonalRedisKey, String.valueOf(chatPersonal.getId()), Objects.requireNonNull(JsonUtil.toJson(chatPersonal))).subscribe();
                }
            });
    }

    public void addChatPersonal(ChatPersonalS2S chatPersonalS2S) {
        long playerId = chatPersonalS2S.getPlayerId();
        long receiverId = chatPersonalS2S.getReceiverId();
        String chatSendPersonalRedisKey = getPersonalRedisKey(playerId);
        String chatReceiverPersonalRedisKey = getPersonalRedisKey(receiverId);
        String chatPersonalRedisKey = getChatPersonalRedisKey(playerId, receiverId);
        ChatPersonal chatPersonal = new ChatPersonal();
        chatPersonal.toChatPersonal(chatPersonalS2S, receiverId);
        this.CHAT.opsForSet().add(chatSendPersonalRedisKey, new String[]{String.valueOf(receiverId)}).subscribe();
        this.CHAT.opsForSet().add(chatReceiverPersonalRedisKey, new String[]{String.valueOf(playerId)}).subscribe();
        this.CHAT.opsForHash().put(chatPersonalRedisKey, String.valueOf(chatPersonal.getId()), Objects.requireNonNull(JsonUtil.toJson(chatPersonal))).subscribe();
        Long size = this.CHAT.opsForHash().size(chatPersonalRedisKey).block();
        if (size == null)
            return;
        if (size > 1000L) {
            List<ChatPersonal> chatPersonalList = getChatPersonalList(playerId, receiverId);
            if (!chatPersonalList.isEmpty()) {
                ChatPersonal firstChatPersonal = chatPersonalList.get(0);
                this.CHAT.opsForHash().remove(chatPersonalRedisKey, new Object[]{String.valueOf(firstChatPersonal.getId())}).subscribe();
            }
        }
    }

    public List<ChatPersonal> getChatPersonalList(long playerId, List<Long> shieldPlayerIds) {
        List<String> block = this.CHAT.opsForSet().members(getPersonalRedisKey(playerId)).collectList().block();
        List<ChatPersonal> chatPersonals = new ArrayList<>();
        if (block != null)
            block.forEach(receiverId -> {
                if (shieldPlayerIds.contains(Long.valueOf(receiverId)))
                    return;
                chatPersonals.addAll(getChatPersonalList(playerId, Long.parseLong(receiverId)));
            });
        List<ChatPersonal> collect = chatPersonals.stream().filter(chatPersonal -> (System.currentTimeMillis() - chatPersonal.getTime() >= TimeUnit.DAYS.toMillis(15L))).collect(Collectors.toList());
        for (ChatPersonal chatPersonal : collect) {
            String chatPersonalRedisKey = getChatPersonalRedisKey(playerId, chatPersonal.getReceiverId());
            this.CHAT.opsForHash().remove(chatPersonalRedisKey, new Object[]{String.valueOf(chatPersonal.getId())}).subscribe();
        }
        chatPersonals.removeIf(chatPersonal -> (System.currentTimeMillis() - chatPersonal.getTime() >= TimeUnit.DAYS.toMillis(15L)));
        return chatPersonals;
    }

    private List<ChatPersonal> getChatPersonalList(long playerId, long receiverId) {
        String chatPersonalRedisKey = getChatPersonalRedisKey(playerId, receiverId);
        List<Map.Entry<Object, Object>> block = this.CHAT.opsForHash().entries(chatPersonalRedisKey).collectList().block();
        List<ChatPersonal> chatPersonals = new ArrayList<>();
        if (block != null)
            block.forEach(entry -> chatPersonals.add(JsonUtil.parseJson(entry.getValue().toString(), ChatPersonal.class)));
        chatPersonals.sort(Comparator.comparingLong(ChatPersonal::getTime));
        return chatPersonals;
    }

    private String getChatPersonalRedisKey(long playerId, long receiverId) {
        return "CHAT_PERSONAL_" + Math.min(playerId, receiverId) + "_" + Math.max(playerId, receiverId);
    }

    private String getPersonalRedisKey(long playerId) {
        return "CHAT_PERSONAL_" + playerId;
    }

    public List<Chat> getChatList(int channel, int uniqueId) {
        String redisKey = getChatRedisKey(channel, uniqueId);
        Long size = this.CHAT.opsForList().size(redisKey).block();
        if (size == null)
            size = 0L;
        if (channel == ChatChannel.UNION.getChannel()) {
            boolean expire = checkUnionExpire(redisKey);
            if (expire) {
                size = this.CHAT.opsForList().size(redisKey).block();
                if (size == null)
                    size = 0L;
            }
        }
        List<String> list = this.CHAT.opsForList().range(redisKey, Math.max(0L, size - 20L), size).collectList().block();
        if (list == null || list.isEmpty())
            return new ArrayList<>();
        List<Chat> chatList = new ArrayList<>();
        list.forEach(string -> chatList.add(JsonUtil.parseJson(string, Chat.class)));
        return chatList;
    }

    private String getChatRedisKey(int channel, int uniqueId) {
        return "CHAT_" + channel + "_" + uniqueId;
    }

    private boolean checkUnionExpire(String redisKey) {
        boolean expire = false;
        Long size = this.CHAT.opsForList().size(redisKey).block();
        if (size == null)
            size = 0L;
        List<String> list = this.CHAT.opsForList().range(redisKey, 0L, size).collectList().block();
        if (list == null || list.isEmpty())
            return false;
        List<Chat> chatList = new ArrayList<>();
        list.forEach(string -> chatList.add(JsonUtil.parseJson(string, Chat.class)));
        long end = chatList.size();
        for (int i = chatList.size() - 1; i >= 0; i--) {
            Chat chat = chatList.get(i);
            if (System.currentTimeMillis() > chat.getTime() + UNION_EXPIRE_TIME) {
                end = i;
                expire = true;
                break;
            }
        }
        if (expire) {
            end = chatList.size() - end;
            this.CHAT.opsForList().trim(redisKey, 0L, end).block();
        }
        return expire;
    }

    public List<Chat> getChatBeforeList(int channel, int uniqueId, long id) {
        String redisKey = getChatRedisKey(channel, uniqueId);
        Long size = this.CHAT.opsForList().size(redisKey).block();
        if (size == null)
            return new ArrayList<>();
        List<String> list = this.CHAT.opsForList().range(redisKey, 0L, size).collectList().block();
        if (list == null || list.isEmpty())
            return new ArrayList<>();
        List<Chat> chatList = new ArrayList<>();
        list.forEach(string -> chatList.add(JsonUtil.parseJson(string, Chat.class)));
        Chat chat = chatList.stream().filter(chatTemp -> (chatTemp.getId() == id)).findFirst().orElse(null);
        if (chat == null)
            return new ArrayList<>();
        int i = chatList.indexOf(chat);
        return chatList.subList((i < 20) ? 0 : (i - 20), i);
    }

    public void addChat(Chat chat, int id) {
        int channel = chat.getChannel();
        String redisKey = getChatRedisKey(channel, id);
        this.CHAT.opsForList().rightPush(redisKey, Objects.requireNonNull(JsonUtil.toJson(chat))).block();
        Long size = this.CHAT.opsForList().size(redisKey).block();
        if (size == null)
            return;
        if (channel == ChatChannel.COUNTRY.getChannel()) {
            if (size > 1000L)
                this.CHAT.opsForList().leftPop(redisKey, Duration.ofSeconds(5L)).subscribe();
        } else if (channel != ChatChannel.UNION.getChannel() &&
                size > 1000L) {
            this.CHAT.opsForList().leftPop(redisKey, Duration.ofSeconds(5L)).subscribe();
        }
    }

    /**
     * 转发消息
     *
     * @param gameId
     * @param message
     */
    public static void brodCastPersonalMessage(int gameId, Message message) {
        ServerId serverId = ServerId.of(ServerType.GAME_SERVER, gameId);
        RpcClientManager.instance.getService(serverId, ChatRemote.class);

    }
}
