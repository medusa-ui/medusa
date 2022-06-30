package io.getmedusa.medusa.core.boot.hydra.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MedusaConfigurationPropertiesTest {
    private final MedusaConfigurationProperties properties = buildProperties("http://localhost:8694", "public123", "private123");

    private MedusaConfigurationProperties buildProperties(final String uri, final String publicKey, final String privateKey) {
        MedusaConfigurationProperties properties = new MedusaConfigurationProperties();
        properties.setHydra(new MedusaConfigurationProperties.Hydra());
        properties.getHydra().setUri(uri);
        properties.getHydra().setSecret(new MedusaConfigurationProperties.Secret());
        properties.getHydra().getSecret().setPublicKey(publicKey);
        properties.getHydra().getSecret().setPrivateKey(privateKey);
        return properties;
    }

    @Test
    void testUrls() {
        Assertions.assertEquals("http://localhost:8694/h/discovery/public123/registration", properties.getHydra().registrationURL());
        Assertions.assertEquals("http://localhost:8694/h/discovery/public123/alive", properties.getHydra().isAliveURL());
        Assertions.assertEquals("http://localhost:8694/h/discovery/public123/requestFragment", properties.getHydra().requestFragmentURL());
    }

}
