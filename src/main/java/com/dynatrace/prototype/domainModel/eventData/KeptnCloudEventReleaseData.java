package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventReleaseData extends KeptnCloudEventDeploymentExtension {
    private LinkedHashMap<String, ?> release;

    public LinkedHashMap<String, ?> getRelease() {
        return release;
    }

    public String getReleaseGitCommit() {
        return getValueOfLinkedHashMap(release, "gitCommit");
    }

/*
    TODO: add test, deployment and evaluation classes probably for all events (evaluation 2 different types)
    private Object deployment; //(deploymentFinishedData)
            private String/Enum deploymentstrategy;
            private String[] deploymentURIsLocal;
            private String[] deploymentURIsPublic;
            private String[] deploymentNames;
            private String gitCommit;

    private Object release; //(releaseData)
            private String gitCommit;

     */
}

