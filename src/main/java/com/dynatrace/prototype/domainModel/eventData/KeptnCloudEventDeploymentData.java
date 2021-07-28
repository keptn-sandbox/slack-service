package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventDeploymentData extends KeptnCloudEventDeploymentExtension {
    private LinkedHashMap<String, ?> configurationChange;

    public LinkedHashMap<String, ?> getConfigurationChange() {
        return configurationChange;
    }

    /*
    TODO: add test, deployment and evaluation classes probably for all events (evaluation 2 different types)
    private Object configurationChange; //(ConfigurationChange)
            private String values; //collection with key value

    private Object deployment; //(DeploymentTriggeredData)
            private String[] deploymentURIsLocal;
            private String[] deploymentURIsPublic;
            private String/Enum deploymentstrategy;

    private Object deployment; //(DeploymentFinishedData)
            //#DeploymentTriggeredData +
            private String[] deploymentNames;
            private String gitCommit;

     */
}

