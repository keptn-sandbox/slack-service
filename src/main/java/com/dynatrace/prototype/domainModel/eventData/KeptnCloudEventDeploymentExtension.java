package com.dynatrace.prototype.domainModel.eventData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class KeptnCloudEventDeploymentExtension extends KeptnCloudEventData {
    private static final String DEPLOYMENT_URIS_PUBLIC = "deploymentURIsPublic", DEPLOYMENT_URIS_LOCAL = "deploymentURIsLocal", DEPLOYMENT_NAMES = "deploymentNames";

    private LinkedHashMap<String, ?> deployment;

    public LinkedHashMap<String, ?> getDeployment() {
        return deployment;
    }

    public ArrayList<String> getDeploymentURIsPublic() {
        return getDeploymentURIs(DEPLOYMENT_URIS_PUBLIC);
    }

    public ArrayList<String> getDeploymentURIsLocal() {
        return getDeploymentURIs(DEPLOYMENT_URIS_LOCAL);
    }

    public String getFirstDeploymentURIPublic() {
        return getFirstDeploymentURI(DEPLOYMENT_URIS_PUBLIC);
    }

    public String getFirstDeploymentURILocal() {
        return getFirstDeploymentURI(DEPLOYMENT_URIS_LOCAL);
    }

    public ArrayList getDeploymentNames() {
        ArrayList deploymentNames = null;

        if (deployment != null) {
            deploymentNames = parseObjectToArrayList(deployment.get(DEPLOYMENT_NAMES));
        }

        return deploymentNames;
    }

    public String getDeploymentGitCommit() {
        return getValueOfLinkedHashMap(deployment, "gitCommit");
    }

    private String getFirstDeploymentURI(String type) {
        String firstURI = null;
        ArrayList deploymentURIs = getDeploymentURIs(type);

        if (deploymentURIs != null) {
            firstURI = Objects.toString(deploymentURIs.get(0));
        }

        return firstURI;
    }

    private ArrayList<String> getDeploymentURIs (String type) {
        ArrayList<String> deploymentURIs = null;
        if (deployment != null) {
            deploymentURIs = parseObjectToArrayList(deployment.get(type));
        }

        return deploymentURIs;
    }
}
