package com.dynatrace.prototype.domainModel;

public enum KeptnEvent {
    SH_KEPTN_EVENT("sh.keptn.event"),

    PROJECT("project.create"), //project.create in specs
    
    SERVICE("service.create"), //service.create in specs

    APPROVAL("approval"),

    DEPLOYMENT("deployment"),

    TEST("test"),

    EVALUATION("evaluation"),

    RELEASE("release"),

    GET_ACTION("get-action"),

    ACTION("action"),

    GET_SLI("get-sli"),

    PROBLEM("problem"),

    ROLLBACK("rollback"),

    TRIGGERED("triggered"),
    STARTED("started"),
    FINISHED("finished");

    private final String value;

    KeptnEvent(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    public String getValue() {
        return value;
    }

}
