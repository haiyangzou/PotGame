package org.pot.common.cipher.rsa;

import org.pot.common.util.MathUtil;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Arrays;

public class RsaCipher {
    public static byte[] encrypt(Rsa.KeyMode keyMode, Rsa.PaddingAlgorithm paddingAlgorithm, final Key publicKey, final byte[] data) throws Exception {
        paddingAlgorithm.checkForEncrypt(keyMode, data);
        int encryptBlockLength = paddingAlgorithm.getEncryptBlockLength(keyMode);
        int decryptBlockLength = paddingAlgorithm.getDecryptBlockLength(keyMode);
        final Cipher cipher = Cipher.getInstance(paddingAlgorithm.name);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        final int blockCount = MathUtil.divideAndCeil(data.length, encryptBlockLength);
        final byte[] outBytes = new byte[blockCount * decryptBlockLength];
        for (int blockIndex = 0; blockIndex < blockCount; blockIndex++) {
            byte[] inBlockBytes = Arrays.copyOfRange(data, blockIndex * encryptBlockLength, Math.min(data.length, (blockIndex + 1) * encryptBlockLength));
            byte[] outBlockBytes = cipher.doFinal(inBlockBytes);
            System.arraycopy(outBlockBytes, 0, outBytes, blockIndex * decryptBlockLength, outBlockBytes.length);
        }
        return outBytes;
    }

    public static byte[] decrypt(Rsa.KeyMode keyMode, Rsa.PaddingAlgorithm paddingAlgorithm, final Key publicKey, final byte[] data) throws Exception {
        paddingAlgorithm.checkForEncrypt(keyMode, data);
        int encryptBlockLength = paddingAlgorithm.getEncryptBlockLength(keyMode);
        int decryptBlockLength = paddingAlgorithm.getDecryptBlockLength(keyMode);
        return null;
    }
}
