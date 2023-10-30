package org.pot.common.net;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustAny {
    public static final SSLSocketFactory TRUST_ANY_SSL_SOCKET_FACTORY;
    public static final HostnameVerifier TRUST_ANY_HOSTNAME_VERIFIER = (s, sslSession) -> true;
    public static final X509TrustManager TRUST_ANY_MANAGER = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    static {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{TRUST_ANY_MANAGER}, null);
            TRUST_ANY_SSL_SOCKET_FACTORY = sslContext.getSocketFactory();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
