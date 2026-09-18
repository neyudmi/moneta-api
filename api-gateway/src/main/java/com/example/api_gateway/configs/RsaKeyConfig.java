package com.example.api_gateway.configs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class RsaKeyConfig {

    private final ResourceLoader resourceLoader;
    private final String publicKeyPath;

    public RsaKeyConfig(
            ResourceLoader resourceLoader,

            @Value("${security.jwt.public-key-path:classpath:keys/public_key.pem}") String publicKeyPath) {
        this.resourceLoader = resourceLoader;
        this.publicKeyPath = publicKeyPath;
    }

    @Bean
    public PublicKey jwtPublicKey() {
        try {
            return KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(loadKeyBytes(publicKeyPath, "PUBLIC KEY")));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create JWT public key.", ex);
        }
    }

    private byte[] loadKeyBytes(String path, String keyType) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        try (InputStream inputStream = resource.getInputStream()) {
            String pem = new String(inputStream.readAllBytes(), StandardCharsets.US_ASCII)
                    .replace("-----BEGIN " + keyType + "-----", "")
                    .replace("-----END " + keyType + "-----", "")
                    .replaceAll("\\s", "");
            return Base64.getDecoder().decode(pem);
        }
    }
}
