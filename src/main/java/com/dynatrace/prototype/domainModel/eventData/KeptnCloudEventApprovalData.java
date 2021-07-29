package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventApprovalData extends KeptnCloudEventData {
    private static final String APPROVAL_PASS = "pass", APPROVAL_WARNING = "warning";
    private LinkedHashMap<String, ?> approval;

    public LinkedHashMap<String, ?> getApproval() {
        return approval;
    }

    public String getApprovalPass() {
        return getValueOfLinkedHashMap(approval, APPROVAL_PASS);
    }

    public String getApprovalWarning() {
        return getValueOfLinkedHashMap(approval, APPROVAL_WARNING);
    }
}
