package com.dynatrace.prototype.domainModel.eventData;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;

public class KeptnCloudEventProblemData extends KeptnCloudEventData {
    public static final String OPEN = "OPEN";
    public static final String RESOLVED = "RESOLVED";

    @SerializedName(value = "State")
    private String state;
    @SerializedName(value = "ProblemID")
    private String problemID;
    @SerializedName(value = "ProblemTitle")
    private String problemTitle;
    @SerializedName(value = "ProblemDetails")
    private LinkedHashMap<String, ?> problemDetails;
    @SerializedName(value = "PID")
    private String pid;
    @SerializedName(value = "ProblemURL")
    private String problemURL;
    @SerializedName(value = "ImpactedEntity")
    private String impactedEntity;
    @SerializedName(value = "ImpactedEntities")
    private Object[] impactedEntities;
    @SerializedName(value = "Tags")
    private String tags;

    public String getState() {
        return state;
    }

    public String getProblemID() {
        return problemID;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public LinkedHashMap<String, ?> getProblemDetails() {
        return problemDetails;
    }

    public String getPid() {
        return pid;
    }

    public String getProblemURL() {
        return problemURL;
    }

    public String getImpactedEntity() {
        return impactedEntity;
    }

    public Object[] getImpactedEntities() {
        return impactedEntities;
    }

    public String getTags() {
        return tags;
    }
}
