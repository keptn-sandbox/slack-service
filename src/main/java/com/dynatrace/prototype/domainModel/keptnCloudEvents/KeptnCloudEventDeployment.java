package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventDeploymentData;

public class KeptnCloudEventDeployment extends KeptnCloudEvent {
    private KeptnCloudEventDeploymentData data;

    public KeptnCloudEventDeployment() { }

    public KeptnCloudEventDeployment(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                     String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                     KeptnCloudEventDeploymentData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventDeploymentData getData() {
        return data;
    }

}
