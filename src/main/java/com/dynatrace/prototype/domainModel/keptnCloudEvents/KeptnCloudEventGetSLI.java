package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventGetSLIData;

public class KeptnCloudEventGetSLI extends KeptnCloudEvent {
    private KeptnCloudEventGetSLIData data;

    public KeptnCloudEventGetSLI() { }

    public KeptnCloudEventGetSLI(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                 String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                 KeptnCloudEventGetSLIData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventGetSLIData getData() {
        return data;
    }

}
