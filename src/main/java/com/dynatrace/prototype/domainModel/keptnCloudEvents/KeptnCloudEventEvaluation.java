package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventEvaluationData;

public class KeptnCloudEventEvaluation extends KeptnCloudEvent {
    private KeptnCloudEventEvaluationData data;

    public KeptnCloudEventEvaluation() { }

    public KeptnCloudEventEvaluation(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                     String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                     KeptnCloudEventEvaluationData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventEvaluationData getData() {
        return data;
    }

}
