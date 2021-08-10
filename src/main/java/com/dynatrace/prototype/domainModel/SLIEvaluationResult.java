package com.dynatrace.prototype.domainModel;

import java.util.LinkedHashSet;

public class SLIEvaluationResult {
    private String displayName;
    private float score;
    private KeptnCloudEventSLIResult value;
    private LinkedHashSet<KeptnCloudEventSLITarget> passTargets;
    private LinkedHashSet<KeptnCloudEventSLITarget> warningTargets;
    private KeptnCloudEventDataResult status;

    public SLIEvaluationResult(String displayName, float score, KeptnCloudEventSLIResult value, LinkedHashSet<KeptnCloudEventSLITarget> passTargets, LinkedHashSet<KeptnCloudEventSLITarget> warningTargets, KeptnCloudEventDataResult status) {
        this.displayName = displayName;
        this.score = score;
        this.value = value;
        this.passTargets = passTargets;
        this.warningTargets = warningTargets;
        this.status = status;
    }

    public float getScore() {
        return score;
    }

    public KeptnCloudEventSLIResult getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LinkedHashSet<KeptnCloudEventSLITarget> getPassTargets() {
        return passTargets;
    }

    public LinkedHashSet<KeptnCloudEventSLITarget> getWarningTargets() {
        return warningTargets;
    }

    public KeptnCloudEventDataResult getStatus() {
        return status;
    }
}
