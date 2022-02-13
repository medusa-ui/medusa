package io.getmedusa.medusa.core.config;

public enum HydraAwakeningType {

    /**
     * default - 3 instances w/ 2 versions, v1 (1, 0% traffic) and v2(2; each 50% traffic)
     */
    NEWEST_VERSION_WINS,

    /**
     * 3 instances w/ 3 versions, last deployed 100% traffic; effectively a failover
     */
    LAST_DEPLOYED_WINS,

    /**
     * 3 instances w/ 3 versions = 33% of traffic each
     */
    EQUAL_SHARE
}
