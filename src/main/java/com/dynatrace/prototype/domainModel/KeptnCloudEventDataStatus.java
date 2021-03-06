package com.dynatrace.prototype.domainModel;

import com.fasterxml.jackson.annotation.JsonValue;

public enum KeptnCloudEventDataStatus {
    SUCCEEDED("succeeded"),
    ERRORED("errored"),
    UNKNOWN("unknown");

    private final String value;

    KeptnCloudEventDataStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
