package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProblemData;

public class KeptnCloudEventProblem extends KeptnCloudEvent {
    private KeptnCloudEventProblemData data;

    public KeptnCloudEventProblem() { }

    public KeptnCloudEventProblem(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                  String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                  KeptnCloudEventProblemData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventProblemData getData() {
        return data;
    }

}
