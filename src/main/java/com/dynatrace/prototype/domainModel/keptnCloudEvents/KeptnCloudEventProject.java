package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProjectData;

public class KeptnCloudEventProject extends KeptnCloudEvent {
    private KeptnCloudEventProjectData data;

    public KeptnCloudEventProject() { }

    public KeptnCloudEventProject(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                                  String datacontenttype, String shkeptncontext, String triggeredid, String time,
                                  KeptnCloudEventProjectData data) {
        super(id, specversion, source, taskName, eventType, datacontenttype, shkeptncontext, triggeredid, time);
        this.data = data;
    }

    public KeptnCloudEventProjectData getData() {
        return data;
    }

}
