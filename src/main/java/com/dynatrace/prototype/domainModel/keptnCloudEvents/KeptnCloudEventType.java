package com.dynatrace.prototype.domainModel.keptnCloudEvents;

public class KeptnCloudEventType {
    private String type;

    public KeptnCloudEventType() { }

    public KeptnCloudEventType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
