package com.dynatrace.prototype.domainModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class KeptnCloudEvent {
    private String specversion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private String source;
    @JsonProperty(value = "type")
    private String fullEventType; //TODO: check if serialized as 'type'
    @JsonIgnore
    private HashMap<String, String> metaData; //includes 'stageName', 'sequenceName' and 'eventType' or 'taskName' and 'eventType'
    private String datacontenttype;
    private Object data; //LinkedHashMap after 1. parsing, subclass of KeptnCloudEventData after 2. parsing
    private String shkeptncontext;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String shkeptnspecversion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String triggeredid; //not available if type equals triggered
    private String time;

    public KeptnCloudEvent() {}

    public KeptnCloudEvent(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType, String datacontenttype, Object data, String shkeptncontext, String triggeredid, String time) {
        this.id = id;
        this.specversion = specversion;
        this.source = source;
        this.fullEventType = KeptnEvent.SH_KEPTN_EVENT.getValue() +"." +taskName.getValue() +"." +eventType.getValue();
        this.datacontenttype = datacontenttype;
        this.data = data;
        this.shkeptncontext = shkeptncontext;
        this.triggeredid = triggeredid;
        this.time = time;
    }

    public String getSpecversion() {
        return specversion;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    @JsonProperty(value = "type")
    public String getFullEventType() {
        return fullEventType;
    }

    @JsonIgnore
    public String getTaskName() {
        String taskName = null;

        if (metaData != null) {
            taskName = metaData.get(KeptnCloudEventValidator.TASK_NAME);
        }

        return taskName;
    }

    @JsonIgnore
    public String getPlainEventType() {
        String eventType = null;

        if (metaData != null) {
            eventType =  metaData.get(KeptnCloudEventValidator.EVENT_TYPE);
        }

        return eventType;
    }

    public void setMetaData(HashMap<String, String> metaData) {
        this.metaData = metaData;
    }

    public String getDatacontenttype() {
        return datacontenttype;
    }

    public String getShkeptncontext() {
        return shkeptncontext;
    }

    public String getShkeptnspecversion() {
        return shkeptnspecversion;
    }

    public String getTriggeredid() {
        return triggeredid;
    }

    public String getTime() {
        return time;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
