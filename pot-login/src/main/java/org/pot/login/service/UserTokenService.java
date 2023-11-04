package org.pot.login.service;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.cipher.SecurityUtil;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.SnappyCompressor;
import org.pot.core.util.RedisUtils;
import org.pot.message.protocol.login.LoginDataS2S;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Service
public class UserTokenService {
    private static final String KEY_PREFIX = RedisUtils.buildRedisKey("GLOBAL", "USER", "TOKEN");
    private static final Compressor compressor = new SnappyCompressor();
    private static final Duration EXPIRE = Duration.ofHours(12);
    @Resource
    private ReactiveStringRedisTemplate globalReactiveRedisTemplate;

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
