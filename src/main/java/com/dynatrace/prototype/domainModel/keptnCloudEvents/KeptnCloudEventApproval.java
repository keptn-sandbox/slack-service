package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventApprovalData;

public class KeptnCloudEventApproval extends KeptnCloudEvent {
    private KeptnCloudEventApprovalData data;

    public KeptnCloudEventApproval() { }

    public KeptnCloudEventApproval(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                   String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                   KeptnCloudEventApprovalData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventApprovalData getData() {
        return data;
    }

}
