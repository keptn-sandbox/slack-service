package com.dynatrace.prototype.domainModel;

import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.List;

public class KeptnCloudEventValidator {
    public static final String STAGE_NAME = "stageName";
    public static final String SEQUENCE_NAME = "sequenceName";
    public static final String EVENT_TYPE = "eventType";
    public static final String TASK_NAME = "taskName";

    private static final String TASK_PROJECT = "project";
    private static final String TASK_SERVICE = "service";
    private static final String TASK_CREATE = "create";

    private static final Splitter splitter = Splitter.on('.');

    /**
     * Parses the given String into a HashMap with the entries 'stageName', 'sequenceName' and 'eventType' if it is
     * a valid event type.
     * @param sequenceTriggeredEventType String to parse
     * @return Filled HashMap if successful or else null
     */
    public static HashMap<String, String> parseSequenceEventType(String sequenceTriggeredEventType) {
        HashMap<String, String> eventMetaData = null;
        List<String> parts = splitter.splitToList(sequenceTriggeredEventType);

        if (isSequenceEventType(parts)) {
            eventMetaData = new HashMap<>();
            eventMetaData.put(STAGE_NAME, parts.get(3));
            eventMetaData.put(SEQUENCE_NAME, parts.get(4));
            eventMetaData.put(EVENT_TYPE, parts.get(5));
        }

        return eventMetaData;
    }

    private static boolean isSequenceEventType(List<String> parts) {
        boolean result = isValidEventType(parts, 6);

        if ((TASK_PROJECT.equals(parts.get(3)) || TASK_SERVICE.equals(parts.get(3))) && TASK_CREATE.equals(parts.get(4))) {
            result = false;
        }

        return result;
    }

    /**
     * Parses the given String into a HashMap if it is a valid event type.
     * The entries are 'taskName' and 'eventType' if it is not a problem event type
     * and only 'taskName' if it is a problem event type
     * @param sequenceTriggeredEventType String to parse
     * @return Filled HashMap if successful or else null
     */
    public static HashMap<String, String> parseTaskEventType(String sequenceTriggeredEventType) {
        HashMap<String, String> eventMetaData = null;
        List<String> parts = splitter.splitToList(sequenceTriggeredEventType);

        if (isValidEventType(parts, 4)) {
            eventMetaData = new HashMap<>();
            eventMetaData.put(TASK_NAME, parts.get(3));

        } else if (isValidEventType(parts, 5)) {
            eventMetaData = new HashMap<>();
            eventMetaData.put(TASK_NAME, parts.get(3));
            eventMetaData.put(EVENT_TYPE, parts.get(4));

        } else if (isValidEventType(parts, 6) &&
                (TASK_PROJECT.equals(parts.get(3)) || TASK_SERVICE.equals(parts.get(3))) && TASK_CREATE.equals(parts.get(4))) {
            eventMetaData = new HashMap<>();
            eventMetaData.put(TASK_NAME, parts.get(3) +"." +parts.get(4));
            eventMetaData.put(EVENT_TYPE, parts.get(5));
        }

        return eventMetaData;
    }

    /**
     * Checks if the given string array is as long as numOfParts and no entries are empty.
     * @param parts of the event type
     * @param numOfParts the check size of parts
     * @return true if the parts represent a valid event type or else false
     */
    private static boolean isValidEventType(List<String> parts, int numOfParts) {
        if (parts.size() != numOfParts) {
            return false;
        }

        for (String part : parts) {
            if ("".equals(part)) {
                return false;
            }
        }

        return true;
    }

}
