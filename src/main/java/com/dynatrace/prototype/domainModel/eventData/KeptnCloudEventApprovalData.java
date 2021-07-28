package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventApprovalData extends KeptnCloudEventData {
    private LinkedHashMap<String, ?> approval;

    public LinkedHashMap<String, ?> getApproval() {
        return approval;
    }

    public String getPass() {
        return getValueOfLinkedHashMap(approval, "pass");
    }

    public String getWarning() {
        return getValueOfLinkedHashMap(approval, "warning");
    }
/*
    TODO: add test, deployment and evaluation classes probably for all events (evaluation 2 different types)
    private Object approval; //(Approval)
            private String/Enum pass;
            private String/Enum warning;

     */
}

