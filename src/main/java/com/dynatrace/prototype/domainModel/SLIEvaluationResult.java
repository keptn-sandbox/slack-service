package com.dynatrace.prototype.domainModel;

import java.util.HashSet;

public class SLIEvaluationResult {
    private String displayName;
    private float score;
    private KeptnCloudEventSLIResult value;
    private HashSet<KeptnCloudEventSLITarget> passTargets; //TODO: maybe LinkedHashSet (Order important?)
    private HashSet<KeptnCloudEventSLITarget> warningTargets;
    private KeptnCloudEventDataResult status;

    public SLIEvaluationResult(String displayName, float score, KeptnCloudEventSLIResult value, HashSet<KeptnCloudEventSLITarget> passTargets, HashSet<KeptnCloudEventSLITarget> warningTargets, KeptnCloudEventDataResult status) {
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

    public HashSet<KeptnCloudEventSLITarget> getPassTargets() {
        return passTargets;
    }

    public HashSet<KeptnCloudEventSLITarget> getWarningTargets() {
        return warningTargets;
    }

    public KeptnCloudEventDataResult getStatus() {
        return status;
    }
}
