package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventReleaseData extends KeptnCloudEventDeploymentExtension {
    private static final String RELEASE_GIT_COMMIT = "gitCommit";
    private LinkedHashMap<String, ?> release;

    public LinkedHashMap<String, ?> getRelease() {
        return release;
    }

    public String getReleaseGitCommit() {
        return getValueOfLinkedHashMap(release, RELEASE_GIT_COMMIT);
    }
}
