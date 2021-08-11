package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventTestData extends KeptnCloudEventDeploymentExtension {
    private static final String TEST_START = "start";
    private static final String TEST_END = "end";
    private static final String TEST_GIT_COMMIT = "gitCommit";
    private LinkedHashMap<String, ?> test;

    public LinkedHashMap<String, ?> getTest() {
        return test;
    }

    public String getTestStart() {
        return getValueOfLinkedHashMap(test, TEST_START);
    }

    public String getTestEnd() {
        return getValueOfLinkedHashMap(test, TEST_END);
    }

    public String getTestGitCommit() {
        return getValueOfLinkedHashMap(test, TEST_GIT_COMMIT);
    }
}
