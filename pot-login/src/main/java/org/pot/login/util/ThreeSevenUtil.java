package org.pot.login.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.binary.Base64Util;
import org.pot.common.cipher.rsa.RsaKeyUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
public class ThreeSevenUtil {
    @Getter
    private static volatile RSAPublicKey SDK_RsaPublicKey;
    @Getter
    private static volatile RSAPrivateKey Adv_RsaPrivateKey;

    static {
        loadSecretKey();
    }

    private static void loadSecretKey() {
        try {
            final ClassLoader classLoader = ThreeSevenUtil.class.getClassLoader();
            SDK_RsaPublicKey = RsaKeyUtil.getPublicKeyFromStream(classLoader.getResourceAsStream("37_key/sdk/publicKey.keystore"));
            Adv_RsaPrivateKey = RsaKeyUtil.getPrivateKeyFromStream(classLoader.getResourceAsStream("37_key/adv/privateKey.keystore"));
        } catch (Exception exception) {
            log.error("Failed to load securet key", exception);
        }

    }

    public static boolean validateSdk(String origin, String sign) {
        if (StringUtils.isAnyBlank(origin, sign)) {
            return false;
        }
        try {
            String digest = decryptSdk(sign);
            return digest.equalsIgnoreCase(DigestUtils.sha1Hex(origin));
        } catch (Exception ex) {
            log.error("check Sign occur An Error");
        }
        return false;
    }

    public static String decryptSdk(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return decrypt(SDK_RsaPublicKey, data);
    }

    public static String decrypt(Key key, String data)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] bytes = Base64Util.decode(data);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] output = cipher.doFinal(bytes);
        return new String(output);
    }
}
