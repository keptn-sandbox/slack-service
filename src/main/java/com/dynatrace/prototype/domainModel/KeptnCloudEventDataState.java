package com.dynatrace.prototype.domainModel;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.zip.DataFormatException;

public enum KeptnCloudEventDataState {
    OPEN("OPEN"),
    RESOLVED("RESOLVED");

    private final String value;

    KeptnCloudEventDataState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static KeptnCloudEventDataState parseResult(String value) throws DataFormatException {
        KeptnCloudEventDataState result;

        if (OPEN.value.equals(value)) {
            result = OPEN;
        } else if (RESOLVED.value.equals(value)) {
            result = RESOLVED;
        } else {
            throw new DataFormatException("Could not parse:" +value +" to KeptnCloudEventDataState!");
        }

        return result;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
