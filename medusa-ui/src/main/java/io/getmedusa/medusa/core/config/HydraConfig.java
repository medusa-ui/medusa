package io.getmedusa.medusa.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

@ConfigurationProperties(prefix = "hydra")
@ConditionalOnProperty(value="hydra.enabled", havingValue = "true")
@Component
public class HydraConfig {

    private boolean enabled;
    private String url;
    private String secret;
    private HydraAwakeningType awakeningType;

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
        return Objects.requireNonNullElse(awakeningType, HydraAwakeningType.NEWEST_VERSION_WINS);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAwakeningType(HydraAwakeningType awakeningType) {
        this.awakeningType = awakeningType;
    }
}
