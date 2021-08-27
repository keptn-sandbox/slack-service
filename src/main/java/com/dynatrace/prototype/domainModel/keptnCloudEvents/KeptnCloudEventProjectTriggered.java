package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProjectTriggeredData;

public class KeptnCloudEventProjectTriggered extends KeptnCloudEvent {
    private KeptnCloudEventProjectTriggeredData data;

    public KeptnCloudEventProjectTriggered() { }

    public KeptnCloudEventProjectTriggered(String id, String specversion, String source, KeptnEvent taskName,
                                           KeptnEvent eventType, String datacontenttype, String shkeptncontext,
                                           String triggeredid, String time, KeptnCloudEventProjectTriggeredData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventProjectTriggeredData getData() {
        return data;
    }

}
