package com.dynatrace.prototype.domainModel;

public class KeptnCloudEventSLIResult {
    String metric;
    float value;
    boolean success;
    String message;

    public KeptnCloudEventSLIResult(String metric, float value, boolean success, String message) {
        this.metric = metric;
        this.value = value;
        this.success = success;
        this.message = message;
    }

    public String getMetric() {
        return metric;
    }

    public float getValue() {
        return value;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
