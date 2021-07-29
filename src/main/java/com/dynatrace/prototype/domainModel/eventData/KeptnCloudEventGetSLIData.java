package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventGetSLIData extends KeptnCloudEventData {
    private static final String SLI_PROVIDER = "sliProvider";
    private LinkedHashMap<String, ?> getSli;
    private LinkedHashMap<String, ?> customFilters;

    public LinkedHashMap<String, ?> getGetSli() {
        return getSli;
    }

    public LinkedHashMap<String, ?> getCustomFilters() {
        return customFilters;
    }

    public String getSliProvider() {
        return getValueOfLinkedHashMap(getSli, SLI_PROVIDER);
    }
}
