package com.dynatrace.prototype.domainModel;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.zip.DataFormatException;

public enum KeptnCloudEventDataResult {
    PASS("pass"),
    WARNING("warning"),
    FAIL("fail");

    private final String value;

    KeptnCloudEventDataResult(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static KeptnCloudEventDataResult parseResult(String value) throws DataFormatException {
        KeptnCloudEventDataResult result;

        if (PASS.value.equals(value)) {
            result = PASS;
        } else if (WARNING.value.equals(value)) {
            result = WARNING;
        } else if (FAIL.value.equals(value)) {
            result = FAIL;
        } else {
            throw new DataFormatException("Could not parse:" +value +" to KeptnCloudEventDataResult!");
        }

        return result;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
