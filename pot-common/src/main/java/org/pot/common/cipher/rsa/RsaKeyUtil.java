package org.pot.common.cipher.rsa;

import com.google.common.io.BaseEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaKeyUtil {
    public static RSAPublicKey getPublicKeyFromStream(final InputStream publicKeyFileInputStream) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return getPublicKey(new String(IOUtils.toByteArray(publicKeyFileInputStream)));
    }

    public static RSAPrivateKey getPrivateKeyFromStream(final InputStream privateKeyFileInputStream) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] privateKeyBytes = IOUtils.toByteArray(privateKeyFileInputStream);
        String privateKeyString = new String(privateKeyBytes);
        return getPrivateKey(privateKeyString);
    }

    public static RSAPrivateKey getPrivateKey(final String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String privateKeyContent = getContent(privateKeyString);
        EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        return (RSAPrivateKey) KeyFactory.getInstance(Rsa.KEY_ALGORITHM_NAME).generatePrivate(encodedKeySpec);
    }

    public static RSAPublicKey getPublicKey(final String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] bytes = BaseEncoding.base64().decode(getContent(publicKeyString));
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(Rsa.KEY_ALGORITHM_NAME);
        return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
    }

    private static String getContent(final String keyString) throws IOException {
        StringBuilder keyContentBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new StringReader(keyString));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (StringUtils.isNotBlank(line) && !line.contains("-")) {
                keyContentBuilder.append(StringUtils.trim(line));
            }
        }
        return keyContentBuilder.toString();
    }
}
