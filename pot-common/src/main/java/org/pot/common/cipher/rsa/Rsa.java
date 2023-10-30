package org.pot.common.cipher.rsa;

public interface Rsa {
    String KEY_ALGORITHM_NAME = "RSA";

    enum PaddingAlgorithm {
        NoPadding("RSA/ECB/NoPadding") {
            @Override
            public void checkForEncrypt(KeyMode keyMode, byte[] data) {
                if (data.length % getEncryptBlockLength(keyMode) != 0)
                    throw new RsaException("Wrong RSA DATA Size");
            }

            @Override
            public int getEncryptBlockLength(KeyMode keyMode) {
                return keyMode.keyBitsLength;
            }

            @Override
            public void checkForDecrypt(KeyMode keyMode, byte[] data) {
                if (data.length % getDecryptBlockLength(keyMode) != 0)
                    throw new RsaException("Wrong RSA DATA Size");
            }

            @Override
            public int getDecryptBlockLength(KeyMode keyMode) {
                return keyMode.keyBytesLength;
            }
        },
        PKCS1Padding("RSA/ECB/PKCS1Padding") {
            @Override
            public void checkForEncrypt(KeyMode keyMode, byte[] data) {
            }

            @Override
            public int getEncryptBlockLength(KeyMode keyMode) {
                return keyMode.keyBytesLength - 11;
            }

            @Override
            public void checkForDecrypt(KeyMode keyMode, byte[] data) {
                if (data.length % getDecryptBlockLength(keyMode) != 0)
                    throw new RsaException("Wrong RSA DATA Size");
            }

            @Override
            public int getDecryptBlockLength(KeyMode keyMode) {
                return keyMode.keyBytesLength;
            }
        };

        public final String name;

        PaddingAlgorithm(String name) {
            this.name = name;
        }

        public abstract void checkForEncrypt(KeyMode keyMode, byte[] data);

        public abstract int getEncryptBlockLength(KeyMode keyMode);

        public abstract void checkForDecrypt(KeyMode keyMode, byte[] data);

        public abstract int getDecryptBlockLength(KeyMode keyMode);
    }

    enum KeyMode {
        BITS_1024(1024),
        BITS_2048(2048);
        public final int keyBitsLength;
        public final int keyBytesLength;

        KeyMode(int keyBitsLength) {
            this.keyBitsLength = keyBitsLength;
            this.keyBytesLength = keyBitsLength / 8;
        }

    }

    enum SignatureAlgorithm {
        MD5withRSA("MD5withRSA"),
        SHA1withRSA("SHA1withRSA"),
        SHA256withRSA("SHA256withRSA"),
        SHA512withRSA("SHA512withRSA");
        public final String name;

        SignatureAlgorithm(String name) {
            this.name = name;
        }
    }
}
