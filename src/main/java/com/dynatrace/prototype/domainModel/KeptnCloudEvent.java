package com.dynatrace.prototype.domainModel;

import com.fasterxml.jackson.annotation.JsonInclude;

public class KeptnCloudEvent {
    private String specversion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private String source;
    private String type;
    private String datacontenttype;
    private Object data; //LinkedHashMap after 1. parsing, subclass of KeptnCloudEventData after 2. parsing
    private String shkeptncontext;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String shkeptnspecversion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String triggeredid; //not available if type equals triggered
    private String time;

    public KeptnCloudEvent() {}

    public KeptnCloudEvent(String id, String specversion, String source, String type, String datacontenttype, Object data, String shkeptncontext, String triggeredid, String time) {
        this.id = id;
        this.specversion = specversion;
        this.source = source;
        this.type = type;
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

    public String getType() {
        return type;
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
