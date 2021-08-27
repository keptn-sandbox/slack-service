package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventTestData;

public class KeptnCloudEventTest extends KeptnCloudEvent {
    private KeptnCloudEventTestData data;

    public KeptnCloudEventTest() { }

    public KeptnCloudEventTest(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                               String datacontenttype, String shkeptncontext, String triggeredid, String time,
                               KeptnCloudEventTestData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventTestData getData() {
        return data;
    }

}
