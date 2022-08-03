package io.getmedusa.medusa.core.boot.hydra.model.meta;

public enum AwakeningType {

    /**
     * default - 3 instances w/ 2 versions, v1 (1, 0% traffic) and v2(2; each 50% traffic)
     */
    NEWEST_VERSION_WINS,

    /**
     * 3 instances w/ 3 versions = 33% of traffic each
     */
    EQUAL_SHARE
}