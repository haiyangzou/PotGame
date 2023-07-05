package org.pot.message.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.tuple.Pair;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import lombok.extern.slf4j.Slf4j;

import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.relect.ConstructorUtil;
import org.pot.common.util.ClassUtil;

@Slf4j
public class ProtocolSupport {
    private static final Map<Class, String> nameMap;
    private static final Map<String, Pair<Class, Parser>> parseMap;

    static {
        try {
            Set<Class<? extends Message>> messageTypeSet = ClassUtil.getSubTypeOf(ProtocolSupport.class, Message.class,
                    ClassUtil::isConcrete);
            final Map<Class, String> tempNameMap = new HashMap<>();
            final Map<String, Pair<Class, Parser>> tempParserMap = new HashMap<>();
            boolean repeated = false;
            for (Class<? extends Message> messageType : messageTypeSet) {
                String messageName = messageType.getSimpleName();
                if (tempParserMap.containsKey(messageName)) {
                    repeated = true;
                    log.error("repeated proto name:{}", messageName);
                }
                if (!ConstructorUtil.containsNoneParamConstructor(messageType)) {
                    log.error("can't found none param constructor in{}", ClassUtil.getAbbreviatedName(messageType));
                    continue;
                }
                Message message = ConstructorUtil.newObjectWithNonParam(messageType);
                Parser<? extends Message> messageParser = message.getParserForType();
                tempParserMap.put(messageName, Pair.of(messageType, messageParser));
                tempNameMap.put(messageType, messageName);
            }
            nameMap = ImmutableMap.copyOf(tempNameMap);
            parseMap = ImmutableMap.copyOf(tempParserMap);
            if (repeated) {
                throw new IllegalStateException("repeated proto error");
            }
        } catch (Exception e) {
            throw new IllegalStateException("init error", e);
        }
    }

    public static String name(Class<? extends Message> protoType) {
        String name = nameMap.get(protoType);
        if (name == null) {
            onNotFound(protoType);
        }
        return name;
    }

    private static void onNotFound(Class<? extends Message> protoType) {
        throw new IllegalArgumentException("ProtoBuf Message Not Found:" + protoType.getName());
    }

    private static void onNotFound(String protoName) {
        throw new IllegalArgumentException("ProtoBuf Message Not Found:" + protoName);
    }

    public static Class<? extends Message> type(String protoName) {
        Pair<Class, Parser> parser = parseMap.get(protoName);
        if (parser == null) {
            onNotFound(protoName);
        }
        return parser.getLeft();
    }

    public static Message parse(String protoName, byte[] data) {
        Pair<Class, Parser> parser = parseMap.get(protoName);
        if (parser == null) {
            onNotFound(protoName);
        }
        try {
            return (Message) parser.getRight().parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ErrorCode buildProtoErrorMsg(IErrorCode code) {
        return buildProtoErrorMsg(code.getErrorCode());
    }

    public static ErrorCode buildProtoErrorMsg(Class<? extends Message> protoType, IErrorCode code) {
        return buildProtoErrorMsg(protoType, code.getErrorCode());
    }

    public static ErrorCode buildProtoErrorMsg(Class<? extends Message> protoType, int errorCode) {
        return ErrorCode.newBuilder().setCode(errorCode).setName(name(protoType)).build();
    }

    public static ErrorCode buildProtoErrorMsg(int errorCode) {
        return ErrorCode.newBuilder().setCode(errorCode).setName("").build();
    }

    public static AckCode buildProtoAckMsg(Class<? extends Message> protoType) {
        return AckCode.newBuilder().setName(name(protoType)).build();
    }

}