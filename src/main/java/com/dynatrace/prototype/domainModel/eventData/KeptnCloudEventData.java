package com.dynatrace.prototype.domainModel.eventData;

import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataStatus;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class KeptnCloudEventData {
    private static final Logger LOG = Logger.getLogger(KeptnCloudEventData.class);

    private String project;
    private String service;
    private String stage;

    private LinkedHashMap<String, String> labels;
    private String message;
    private KeptnCloudEventDataStatus status;
    private KeptnCloudEventDataResult result;

    public KeptnCloudEventData() {}

    public KeptnCloudEventData(String project, String service, String stage, LinkedHashMap<String, String> labels, String message, KeptnCloudEventDataStatus status, KeptnCloudEventDataResult result) {
        this.project = project;
        this.service = service;
        this.stage = stage;
        this.labels = labels;
        this.message = message;
        this.status = status;
        this.result = result;
    }

    public String getProject() {
        return project;
    }

    public String getService() {
        return service;
    }

    public String getStage() {
        return stage;
    }

    public LinkedHashMap<String, String> getLabels() {
        return labels;
    }

    public String getMessage() {
        return message;
    }

    public KeptnCloudEventDataStatus getStatus() {
        return status;
    }

    public KeptnCloudEventDataResult getResult() {
        return result;
    }

    /**
     * Return the value of the first matching key as a string or else null.
     *
     * @param key String array of keys to search.
     * @return String or null
     */
    protected String getValueOfLinkedHashMap(LinkedHashMap<String, ?> linkedHashMap, String... key) {
        String result = null;
        int i = 0;

        if (linkedHashMap != null && !linkedHashMap.isEmpty()) {
            while (result == null && i < key.length) {
                result = Objects.toString(linkedHashMap.get(key[i]));
                i++;
            }
        } else {
            LOG.warn("Cannot perform linkedHashMap.get() when it is null or empty!");
        }

        return result;
    }

    /**
     * Checks if the given object is an ArrayList and if it returns it as such,
     * else it returns null.
     * @param object potential ArrayList
     * @return The object as ArrayList or null
     */
    protected ArrayList parseObjectToArrayList (Object object) {
        ArrayList result = null;

        if (object instanceof ArrayList) {
            result = (ArrayList) object;
        }

        return result;
    }
}
