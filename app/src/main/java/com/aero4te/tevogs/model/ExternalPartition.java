package com.aero4te.tevogs.model;

public enum ExternalPartition {
    Public(1),
    Private(2);

    private final int externalPartition;

    ExternalPartition(int externalPartition) {
        this.externalPartition = externalPartition;
    }
}
