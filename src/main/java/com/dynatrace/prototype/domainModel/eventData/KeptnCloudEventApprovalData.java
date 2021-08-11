package com.dynatrace.prototype.domainModel.eventData;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;

public class KeptnCloudEventApprovalData extends KeptnCloudEventData {
    private static final String APPROVAL_PASS = "pass", APPROVAL_WARNING = "warning";
    private LinkedHashMap<String, ?> approval;

    public LinkedHashMap<String, ?> getApproval() {
        return approval;
    }

    @JsonIgnore
    public String getApprovalPass() {
        return getValueOfLinkedHashMap(approval, APPROVAL_PASS);
    }

    @JsonIgnore
    public String getApprovalWarning() {
        return getValueOfLinkedHashMap(approval, APPROVAL_WARNING);
    }
}
