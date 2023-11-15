package org.pot.common.id;

import org.pot.common.communication.server.ServerId;
import org.pot.common.util.RandomUtil;

public class UniqueIdUtil {
    private static final int UID_BITS = 50;
    private static final long MAX_UID = (1L << UID_BITS) - 1L;
    private static final int SERVER_ID_BITS = 13;
    public static final int MAX_SERVER_ID = (1 << SERVER_ID_BITS) - 1;
    private static final TimeIdGenerator timeIdGenerator = new TimeIdGenerator(5);

    public static long getUid(long uniqueId) {
        return uniqueId >> SERVER_ID_BITS;
    }

    public static int index(long uniqueId, int buckets) {
        long uid = UniqueIdUtil.getUid(uniqueId) >> 3;
        return (int) Math.abs(uid % buckets);
    }

    public static int getServerId(long uniqueId) {
        return (int) (uniqueId & MAX_SERVER_ID);
    }

    public static long newUniqueId(ServerId serverId) {
        final long timeIdBitsNum = timeIdGenerator.getIdBitsNum();
        final long typeAndIdBitsNum = IdGenerator.MAX_ID_BITS_NUM - timeIdBitsNum;
        if (typeAndIdBitsNum <= 0) {
            throw new IllegalArgumentException(String.format("invalid bits %s", typeAndIdBitsNum));
        }
        final long typeBitsNum = typeAndIdBitsNum - SERVER_ID_BITS;
        if (typeBitsNum < 0) {
            throw new IllegalArgumentException(String.format("invalid type %s", typeBitsNum));
        }
        final long maxServerType = (1L << typeBitsNum) - 1L;
        if (serverId.serverType.getId() < 0 || serverId.serverType.getId() > maxServerType) {
            throw new IllegalArgumentException(String.format("%s is invalid serverType ", serverId));
        }
        if (serverId.id < 1 || serverId.id > MAX_SERVER_ID) {
            throw new IllegalArgumentException(String.format("%s is invalid serverId ", serverId));
        }
        return (timeIdGenerator.nextId() << typeBitsNum) | (serverId.serverType.getId() << SERVER_ID_BITS) | (serverId.id & MAX_SERVER_ID);
    }

    public static long newUniqueId(long uid, int serverId) {
        if (uid < 1 || uid > MAX_UID) {
            throw new IllegalArgumentException(String.format("%s is invalid uid ", uid));
        }
        if (serverId < 1 || serverId > MAX_SERVER_ID) {
            throw new IllegalArgumentException(String.format("%s is invalid serverId ", serverId));
        }
        return (uid << SERVER_ID_BITS) | (serverId & MAX_SERVER_ID);
    }

    public static String newUuid() {
        return RandomUtil.uuid();
    }

    public static void init() {

    }
}
