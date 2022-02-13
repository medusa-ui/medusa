package io.getmedusa.medusa.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "hydra")
@ConstructorBinding
public class HydraConfig {

    private boolean enabled;
    private String url;
    private String secret;
    private HydraAwakeningType awakeningType;

    public HydraConfig(boolean enabled, String url, String secret, HydraAwakeningType awakeningType) {
        this.enabled = enabled;
        this.url = url;
        this.awakeningType = awakeningType;

        if(this.awakeningType == null) {
            this.awakeningType = HydraAwakeningType.NEWEST_VERSION_WINS;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getUrl() {
        return url;
    }

    public String getSecret() {
        return secret;
    }

    public HydraAwakeningType getAwakeningType() {
        return awakeningType;
    }
}
