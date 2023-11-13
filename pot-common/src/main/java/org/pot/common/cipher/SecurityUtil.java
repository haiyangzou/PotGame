package org.pot.common.cipher;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.pot.common.cipher.rsa.Rsa;
import org.pot.common.cipher.rsa.RsaCipher;
import org.pot.common.cipher.rsa.RsaKeyUtil;
import org.pot.common.cipher.rsa.RsaSignatureUtil;
import org.pot.common.file.FileUtil;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

@Slf4j
public class SecurityUtil {
    private static final Rsa.KeyMode RSA_KEY_MODE = Rsa.KeyMode.BITS_2048;
    private static final Rsa.PaddingAlgorithm RSA_PADDING_ALGORITHM = Rsa.PaddingAlgorithm.PKCS1Padding;
    private static final Rsa.SignatureAlgorithm RSA_SIGNATURE_ALGORITHM = Rsa.SignatureAlgorithm.MD5withRSA;

    private static volatile RSAPublicKey rsaPublicKey;
    private static volatile RSAPrivateKey rsaPrivateKey;
    private static volatile byte[] ideaSecretKey;
    private static final String KEY_DIR_PREFIX = "utils_security_tool_keys_20210902";

    static {
        loadSecretKey();
    }

    public static void loadSecretKey() {
        try {
            final ClassLoader classLoader = SecurityUtil.class.getClassLoader();
            final String dir = KEY_DIR_PREFIX + "20210902/";
            ideaSecretKey = IOUtils.toByteArray(Objects.requireNonNull(classLoader.getResourceAsStream(dir + "idea.key")));
            rsaPublicKey = RsaKeyUtil.getPublicKeyFromStream(classLoader.getResourceAsStream(dir + "rsa/public-rsa.pem"));
            rsaPrivateKey = RsaKeyUtil.getPrivateKeyFromStream(classLoader.getResourceAsStream(dir + "rsa/private-rsa.pem"));
        } catch (Exception exception) {
            log.error("SecurityTool Failed to load key", exception);
        }
    }

    public static void generateSecretKey() throws Exception {
        String dir = "src/main/resources/" + KEY_DIR_PREFIX + "" + "20210902/";
        byte[] desSecretKey = RandomUtils.nextBytes(8);
        FileUtil.write(dir + "des.key", desSecretKey, false);
        byte[] ideaSecretKey = RandomUtils.nextBytes(16);
        FileUtil.write(dir + "idea.key", ideaSecretKey, false);
//        final Pair<RSAPublicKey, RSAPrivateKey> keyPair = RsaKeyUtil.generateKey(RSA_KEY_MODE);
//        TextFileUtil.write(RsaKeyUtil.saveKey(keyPair.getLeft()), dir + "rsa/public-rsa.pem", false, false);
//        TextFileUtil.write(RsaKeyUtil.saveKey(keyPair.getRight()), dir + "rsa/private-rsa.pem", false, false);

    }

    public static String decryptIdea(final String data) {
        return IdeaCipher.decrypt(ideaSecretKey, data);
    }

    public static String encryptIdea(final String data) {
        return IdeaCipher.encrypt(ideaSecretKey, data);
    }

    public static byte[] decryptRsa(final byte[] data) throws Exception {
        return RsaCipher.decrypt(RSA_KEY_MODE, RSA_PADDING_ALGORITHM, rsaPrivateKey, data);
    }

    public static byte[] encryptRsa(final byte[] data) throws Exception {
        return RsaCipher.encrypt(RSA_KEY_MODE, RSA_PADDING_ALGORITHM, rsaPublicKey, data);
    }

    public static String signRsa(final String data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return RsaSignatureUtil.sign(RSA_SIGNATURE_ALGORITHM, rsaPrivateKey, data);

    }
}
