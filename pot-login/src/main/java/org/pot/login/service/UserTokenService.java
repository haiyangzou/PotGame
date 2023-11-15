package org.pot.login.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pot.common.cipher.SecurityUtil;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.SnappyCompressor;
import org.pot.dal.RedisUtils;
import org.pot.message.protocol.login.LoginDataS2S;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Service
public class UserTokenService {
    private static final String KEY_PREFIX = RedisUtils.buildRedisKey("GLOBAL", "USER", "TOKEN");
    private static final Compressor compressor = new SnappyCompressor();
    private static final long START_TIME = System.currentTimeMillis();
    private static final Duration EXPIRE = Duration.ofHours(12);
    @Resource
    private ReactiveStringRedisTemplate globalReactiveRedisTemplate;

    public LoginDataS2S getTokenData(String tokenKey) throws Exception {
        if (StringUtils.isBlank(tokenKey)) {
            return null;
        }
        String result = SecurityUtil.decryptIdea(tokenKey);
        String[] content = StringUtils.split(result, "|");
        if (ArrayUtils.isEmpty(content)) return null;
        if (StringUtils.isBlank(content[0]) || !NumberUtils.isParsable(content[0])) return null;
        if (StringUtils.isBlank(content[1]) || !NumberUtils.isParsable(content[1])) return null;
        if (StringUtils.isBlank(content[2]) || !NumberUtils.isParsable(content[2])) return null;
        long time = NumberUtils.toLong(content[1]);
        long uid = NumberUtils.toLong(content[2]);
        if (time < START_TIME) return null;
        String tokenRedisKey = RedisUtils.buildRedisKey(KEY_PREFIX, uid);
        String tokenRedisData = globalReactiveRedisTemplate.opsForValue().get(tokenRedisKey).block();
        if (StringUtils.isBlank(tokenRedisData)) {
            return null;
        }
        byte[] bytes = compressor.decompressBase64StringToBytes(tokenRedisData);
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        LoginDataS2S loginDataS2S = LoginDataS2S.parseFrom(bytes);
        if (!StringUtils.equals(tokenKey, loginDataS2S.getToken())) {
            return null;
        }
        loginDataS2S = loginDataS2S.toBuilder().setToken(generaTokenKey(uid)).build();
        addTokenData(uid, loginDataS2S);
        return loginDataS2S;
    }

    public String generaTokenKey(long uid) {
        String seed = String.valueOf(RandomUtils.nextLong());
        String time = String.valueOf(System.currentTimeMillis());
        return SecurityUtil.encryptIdea(StringUtils.joinWith("|", seed, time, uid));
    }

    public void addTokenData(long uid, LoginDataS2S loginDataS2S) throws Exception {
        if (loginDataS2S == null) return;
        String tokenRedisKey = RedisUtils.buildRedisKey(KEY_PREFIX, uid);
        String tokenRedisData = compressor.compressToBase64String(loginDataS2S.toByteArray());
        globalReactiveRedisTemplate.opsForValue().set(tokenRedisKey, tokenRedisData).subscribe();
        globalReactiveRedisTemplate.expire(tokenRedisKey, EXPIRE).subscribe();
    }
}
