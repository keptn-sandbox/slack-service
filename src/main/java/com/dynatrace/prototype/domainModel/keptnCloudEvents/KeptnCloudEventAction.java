package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventActionData;

public class KeptnCloudEventAction extends KeptnCloudEvent {
    private KeptnCloudEventActionData data;

    public KeptnCloudEventAction() { }

    public KeptnCloudEventAction(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                 String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                 KeptnCloudEventActionData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventActionData getData() {
        return data;
    }

}
