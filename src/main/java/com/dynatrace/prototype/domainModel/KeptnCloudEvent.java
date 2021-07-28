package com.dynatrace.prototype.domainModel;

public class KeptnCloudEvent {
    private String specversion;
    private String id;
    private String source;
    private String type;
    private String datacontenttype;
    private Object data; //LinkedHashMap after 1. parsing, subclass of KeptnCloudEventData after 2. parsing
    private String shkeptncontext;
    private String triggeredid;         //not available if type equals triggered
    private String time;

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
