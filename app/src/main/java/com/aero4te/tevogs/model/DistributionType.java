package com.aero4te.tevogs.model;

public enum DistributionType {
    Multicast(1),
    Unicast  (2),
    Broadcast(3);

    private final int distributionType;

    private DistributionType(int distributionType) {
        this.distributionType = distributionType;
    }
}
