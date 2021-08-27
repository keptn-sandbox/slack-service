package com.dynatrace.prototype.domainModel.keptnCloudEvents;

import com.dynatrace.prototype.domainModel.KeptnCloudEventValidator;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public abstract class KeptnCloudEvent<T extends KeptnCloudEventData> {
    private String specversion;
    private String id;
    private String source;
    @SerializedName(value = "type")
    private String fullEventType;
    private transient HashMap<String, String> metaData; //includes 'stageName', 'sequenceName' and 'eventType' or 'taskName' and 'eventType'
    @SerializedName(value = "datacontenttype", alternate = {"contenttype"})
    private String datacontenttype;
    private String shkeptncontext;
    private String shkeptnspecversion;
    private String triggeredid; //not available if type equals triggered
    private String time;

    public KeptnCloudEvent() {}

    public KeptnCloudEvent(String id, String specversion, String source, KeptnEvent taskName, KeptnEvent eventType,
                           String datacontenttype, String shkeptncontext, String triggeredid, String time) {
        this.id = id;
        this.specversion = specversion;
        this.source = source;
        this.fullEventType = KeptnEvent.SH_KEPTN_EVENT.getValue() + "." + taskName.getValue() + "." + eventType.getValue();
        this.datacontenttype = datacontenttype;
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

//    @JsonProperty(value = "type")
    public String getFullEventType() {
        return fullEventType;
    }

//    @JsonIgnore
    public String getTaskName() {
        return getSpecificMetaData(KeptnCloudEventValidator.TASK_NAME);
    }

//    @JsonIgnore
    public String getPlainEventType() {
        return getSpecificMetaData(KeptnCloudEventValidator.EVENT_TYPE);
    }

//    @JsonIgnore
    public String getStageName() {
        return getSpecificMetaData(KeptnCloudEventValidator.STAGE_NAME);
    }

//    @JsonIgnore
    public String getSequenceName() {
        return getSpecificMetaData(KeptnCloudEventValidator.SEQUENCE_NAME);
    }

    /**
     * Returns the value of the given field from the metaData HashMap if successful, otherwise null.
     * Null is possible if the field does not exists, its value is null or the HashMap metaData is null.
     *
     * @param field of metaData
     * @return the value of field or else null
     */
//    @JsonIgnore
    private String getSpecificMetaData(String field) {
        String eventType = null;

        if (metaData != null) {
            eventType = metaData.get(field);
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

    public abstract T getData();
}
