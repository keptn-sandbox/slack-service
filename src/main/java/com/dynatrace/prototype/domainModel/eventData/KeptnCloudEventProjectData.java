package com.dynatrace.prototype.domainModel.eventData;

/**
 * The ProjectData class to use for every Project event except .triggered!
 */
public class KeptnCloudEventProjectData extends KeptnCloudEventData {
    private KeptnCloudEventProjectFinishedData createdProject;

    public KeptnCloudEventProjectFinishedData getCreatedProject() {
        return createdProject;
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
