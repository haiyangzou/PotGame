package org.pot.common.cipher;


import org.pot.common.cipher.rsa.Rsa;
import org.pot.common.cipher.rsa.RsaCipher;
import org.pot.common.cipher.rsa.RsaSignatureUtil;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class SecurityUtil {
    private static final Rsa.KeyMode RSA_KEY_MODE = Rsa.KeyMode.BITS_2048;
    private static final Rsa.PaddingAlgorithm RSA_PADDING_ALGORITHM = Rsa.PaddingAlgorithm.PKCS1Padding;
    private static final Rsa.SignatureAlgorithm RSA_SIGNATURE_ALGORITHM = Rsa.SignatureAlgorithm.MD5withRSA;

    private static volatile RSAPublicKey rsaPublicKey;
    private static volatile RSAPrivateKey rsaPrivateKey;
    private static volatile byte[] ideaSecretKey;

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
