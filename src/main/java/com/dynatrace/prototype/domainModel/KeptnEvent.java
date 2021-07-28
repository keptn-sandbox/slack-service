package com.dynatrace.prototype.domainModel;

public enum KeptnEvent {
    SH_KEPTN_EVENT("sh.keptn.event"),
    TRIGGERED(".triggered", "Triggered"),
    STARTED(".started", "Started"),
    FINISHED(".finished", "Finished"),
    //TODO: what about status changed?
    //TODO: maybe remove triggered, started and finished of the different events

    PROJECT(SH_KEPTN_EVENT.value +".project.create", "Project create"), //project.create in specs
    PROJECT_TRIGGERED(PROJECT.value +TRIGGERED.value),
    PROJECT_STARTED(PROJECT.value +STARTED.value),
    PROJECT_FINISHED(PROJECT.value +FINISHED.value),
    
    SERVICE(SH_KEPTN_EVENT.value +".service.create", "Service create"), //service.create in specs

    APPROVAL(SH_KEPTN_EVENT.value +".approval", "Approval"),
    APPROVAL_TRIGGERED(APPROVAL.value +TRIGGERED.value),
    APPROVAL_STARTED(APPROVAL.value +STARTED.value),
    APPROVAL_FINISHED(APPROVAL.value +FINISHED.value),

    DEPLOYMENT(SH_KEPTN_EVENT.value +".deployment", "Deployment"),
    DEPLOYMENT_TRIGGERED(DEPLOYMENT.value +TRIGGERED.value),
    DEPLOYMENT_STARTED(DEPLOYMENT.value +STARTED.value),
    DEPLOYMENT_FINISHED(DEPLOYMENT.value +FINISHED.value),

    TEST(SH_KEPTN_EVENT.value +".test", "Test"),
    TEST_TRIGGERED(TEST.value +TRIGGERED.value),
    TEST_STARTED(TEST.value +STARTED.value),
    TEST_FINISHED(TEST.value +FINISHED.value),

    EVALUATION(SH_KEPTN_EVENT.value +".evaluation", "Evaluation"),
    EVALUATION_TRIGGERED(EVALUATION.value +TRIGGERED.value),
    EVALUATION_STARTED(EVALUATION.value +STARTED.value),
    EVALUATION_FINISHED(EVALUATION.value +FINISHED.value),

    RELEASE(SH_KEPTN_EVENT.value +".release", "Release"),
    RELEASE_TRIGGERED(RELEASE.value +TRIGGERED.value),
    RELEASE_STARTED(RELEASE.value +STARTED.value),
    RELEASE_FINISHED(RELEASE.value +FINISHED.value),

    GET_ACTION(SH_KEPTN_EVENT.value +".get-action", "Get Action"),
    GET_ACTION_TRIGGERED(GET_ACTION.value +TRIGGERED.value),
    GET_ACTION_STARTED(GET_ACTION.value +STARTED.value),
    GET_ACTION_FINISHED(GET_ACTION.value +FINISHED.value),

    ACTION(SH_KEPTN_EVENT.value +".action", "Action"),
    ACTION_TRIGGERED(ACTION.value +TRIGGERED.value),
    ACTION_STARTED(ACTION.value +STARTED.value),
    ACTION_FINISHED(ACTION.value +FINISHED.value),

    GET_SLI(SH_KEPTN_EVENT.value +".get-sli", "Get SLI"),

    PROBLEM(SH_KEPTN_EVENT.value +".problem", "Problem"),

    ROLLBACK(SH_KEPTN_EVENT.value +".rollback", "Rollback"),
    ROLLBACK_TRIGGERED(ROLLBACK.value +TRIGGERED.value),
    ROLLBACK_STARTED(ROLLBACK.value +STARTED.value),
    ROLLBACK_FINISHED(ROLLBACK.value +FINISHED.value);

    private final String value;
    private String name = null;

    KeptnEvent(String value) {
        this.value = value;
    }

    KeptnEvent(String value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String toString() {
        return getValue();
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
