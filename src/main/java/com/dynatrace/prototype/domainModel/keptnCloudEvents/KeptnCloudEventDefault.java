package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;

public class KeptnCloudEventDefault extends KeptnCloudEvent {
    private KeptnCloudEventData data;

    public KeptnCloudEventDefault() { }

    public KeptnCloudEventDefault(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                  String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                  KeptnCloudEventData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventData getData() {
        return data;
    }
    
}
