package com.dynatrace.prototype.domainModel.eventData;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;

public class KeptnCloudEventGetSLIData extends KeptnCloudEventData {
    private static final String SLI_PROVIDER = "sliProvider";
    @JsonProperty(value = "get-sli")
    private LinkedHashMap<String, ?> getSli;
    private LinkedHashMap<String, ?> customFilters;
    private String deployment;

    public LinkedHashMap<String, ?> getGetSli() {
        return getSli;
    }

    public LinkedHashMap<String, ?> getCustomFilters() {
        return customFilters;
    }

    public String getDeployment() {
        return deployment;
    }

    public String getSliProvider() {
        return getValueOfLinkedHashMap(getSli, SLI_PROVIDER);
    }
}
