package com.dynatrace.prototype.domainModel;

public class KeptnCloudEventSLITarget {
    private String criteria;
    private float targetValue;
    private boolean violated;

    public KeptnCloudEventSLITarget(String criteria, float targetValue, boolean violated) {
        this.criteria = criteria;
        this.targetValue = targetValue;
        this.violated = violated;
    }

    public String getCriteria() {
        return criteria;
    }

    public float getTargetValue() {
        return targetValue;
    }

    public boolean isViolated() {
        return violated;
    }

    @Override
    public String toString() {
        return "criteria='" + criteria + '\'' +
                ", targetValue=" + targetValue +
                ", violated=" + violated;
    }
}
