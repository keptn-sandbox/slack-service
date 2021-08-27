package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventReleaseData;

public class KeptnCloudEventRelease extends KeptnCloudEvent {
    private KeptnCloudEventReleaseData data;

    public KeptnCloudEventRelease() { }

    public KeptnCloudEventRelease(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                  String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                  KeptnCloudEventReleaseData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventReleaseData getData() {
        return data;
    }

}
