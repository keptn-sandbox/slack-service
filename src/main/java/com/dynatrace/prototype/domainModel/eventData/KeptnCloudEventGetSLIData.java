package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventGetSLIData extends KeptnCloudEventData {
    private LinkedHashMap<String, ?> getSli;
    private LinkedHashMap<String, ?> customFilters;

    public LinkedHashMap<String, ?> getGetSli() {
        return getSli;
    }

    public LinkedHashMap<String, ?> getCustomFilters() {
        return customFilters;
    }

    public String getSliProvider() {
        return getValueOfLinkedHashMap(getSli, "sliProvider");
    }

    /*
    //Get-SLI
    private Object getSli; //(GetSLI)
            private String sliProvider;
            private String start;
            private String end;
            private String[] indicators;

    private Object customFilters; //(SLIFilter)
            private HashMap<String, String> key_value
                    private String key;
                    private String value;

     */
}

