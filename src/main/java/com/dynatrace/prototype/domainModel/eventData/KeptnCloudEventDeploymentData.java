package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventDeploymentData extends KeptnCloudEventDeploymentExtension {
    private LinkedHashMap<String, ?> configurationChange;

    public LinkedHashMap<String, ?> getConfigurationChange() {
        return configurationChange;
    }
}
