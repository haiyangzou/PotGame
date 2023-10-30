package org.pot.common.cipher.rsa;

import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;

public class RsaSignatureUtil {
    public static String sign(final Rsa.SignatureAlgorithm signatureAlgorithm, final RSAPrivateKey rsaPrivateKey, final String data)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return sign(signatureAlgorithm, rsaPrivateKey, data.getBytes(StandardCharsets.UTF_8));
    }

    public static String sign(final Rsa.SignatureAlgorithm signatureAlgorithm, final RSAPrivateKey rsaPrivateKey, final byte[] data)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(signatureAlgorithm.name);
        signature.initSign(rsaPrivateKey);
        signature.update(data);
        return BaseEncoding.base64().encode(signature.sign());
    }

}
