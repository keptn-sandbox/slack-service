package com.dynatrace.prototype.domainModel.eventData;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;

public class KeptnCloudEventProblemData extends KeptnCloudEventData {
    public static final String OPEN = "OPEN";
    public static final String RESOLVED = "RESOLVED";

    @JsonProperty(value = "State")
    private String state;
    @JsonProperty(value = "ProblemID")
    private String problemID;
    @JsonProperty(value = "ProblemTitle")
    private String problemTitle;
    @JsonProperty(value = "ProblemDetails")
    private LinkedHashMap<String, ?> problemDetails;
    @JsonProperty(value = "PID")
    private String pid;
    @JsonProperty(value = "ProblemURL")
    private String problemURL;
    @JsonProperty(value = "ImpactedEntity")
    private String impactedEntity;
    @JsonProperty(value = "Tags")
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

    public String getTags() {
        return tags;
    }
}
