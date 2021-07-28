package com.dynatrace.prototype.domainModel.eventData;

/**
 * The inner part of the ProjectData class.
 * No inheritance!
 */
public class KeptnCloudEventProjectFinishedData {
    private String projectName;
    private String gitRemoteURL;
    private String shipyard;

    public String getProjectName() {
        return projectName;
    }

    public String getGitRemoteURL() {
        return gitRemoteURL;
    }

    public String getShipyard() {
        return shipyard;
    }

    /*
    //Project create
    private String projectName;
    private String gitRemoteURL;
    private String shipyard;

    private Object createdProject;
            private String projectName;
            private String gitRemoteURL;
            private String shipyard;

     */
}
