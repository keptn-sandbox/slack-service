package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventTestData extends KeptnCloudEventDeploymentExtension {
    private LinkedHashMap<String, ?> test;

    public LinkedHashMap<String, ?> getTest() {
        return test;
    }

    public String getTestStart() {
        return getValueOfLinkedHashMap(test, "start");
    }

    public String getTestEnd() {
        return getValueOfLinkedHashMap(test, "end");
    }

    public String getTestGitCommit() {
        return getValueOfLinkedHashMap(test, "gitCommit");
    }

    /*
    TODO: add test, deployment and evaluation classes probably for all events (evaluation 2 different types)
    private Object deployment; //(TestTriggeredDeploymentDetails)
            private String[] deploymentURIsLocal;
            private String[] deploymentURIsPublic;

    private Object test; //(TestTriggeredDetails)
            private String/Enum teststrategy;

    private Object test; //(TestFinishedDetails)
            private String start;
            private String end;
            private String gitCommit;

     */
}

