package io.getmedusa.medusa.core.boot.hydra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "medusa")
public class MedusaConfigurationProperties {
    /**
     * Hydra configuration
     */
    @NestedConfigurationProperty
    private Hydra hydra = new Hydra();

    /**
     * The application name
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hydra getHydra() {
        return hydra;
    }

    public void setHydra(Hydra hydra) {
        this.hydra = hydra;
    }

    public static class Hydra{
        /**
         * Shared connection Secret
         */
        private Secret secret = new Secret();
        /**
         * The connection URI
         */
        private String uri;

        public Secret getSecret() {
            return secret;
        }

        public void setSecret(Secret secret) {
            this.secret = secret;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String registrationURL(){
            return uri +
                    "/h/discovery/" +
                    secret.publicKey +
                    "/registration";
        }

        public String isAliveURL(){
            return uri +
                    "/h/discovery/" +
                    secret.publicKey +
                    "/alive";
        }

        public String requestFragmentURL(){
            return uri +
                    "/h/discovery/" +
                    secret.publicKey +
                    "/requestFragment";
        }
    }

    public static class Secret {
        /**
         * Public key
         */
        private String publicKey;
        /**
         * Private key
         */
        private String privateKey;

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
    }
}
