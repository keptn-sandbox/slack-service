package com.dynatrace.prototype.domainModel;

import java.util.HashMap;

public class KeptnCloudEventValidator {
    public static final String STAGE_NAME = "stageName";
    public static final String SEQUENCE_NAME = "sequenceName";
    public static final String EVENT_TYPE = "eventType";
    public static final String TASK_NAME = "taskName";

    private static final String TASK_PROJECT = "project";
    private static final String TASK_SERVICE = "service";

    private static boolean isSequenceEventType(String[] parts) {
        boolean result = isValidEventType(parts, 6);
        int i = 0;

        while (result && i < parts.length) {
            if (TASK_PROJECT.equals(parts[i]) || TASK_SERVICE.equals(parts[i])) {
                result = false;
            }

            i++;
        }

        return result;
    }

    /**
     * Parses the given String into a HashMap with the entries 'stageName', 'sequenceName' and 'eventType' if it is
     * a valid event type.
     * @param sequenceTriggeredEventType String to parse
     * @return Filles HashMap if successful or else null
     */
    public static HashMap<String, String> parseSequenceEventType(String sequenceTriggeredEventType) {
        HashMap<String, String> eventMetaData = null;
        String[] parts = sequenceTriggeredEventType.split("\\.");

        if (isSequenceEventType(parts)) {
            eventMetaData = new HashMap<>();
            eventMetaData.put(STAGE_NAME, parts[3]);
            eventMetaData.put(SEQUENCE_NAME, parts[4]);
            eventMetaData.put(EVENT_TYPE, parts[5]);
        }

        return eventMetaData;
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
        String[] parts = sequenceTriggeredEventType.split("\\.");

        if (isValidEventType(parts, 4)) {
            eventMetaData = new HashMap<>();
            eventMetaData.put(TASK_NAME, parts[3]);

        } else if (isValidEventType(parts, 5)) {
            eventMetaData = new HashMap<>();
            eventMetaData.put(TASK_NAME, parts[3]);
            eventMetaData.put(EVENT_TYPE, parts[4]);

        } else if (isValidEventType(parts, 6)) {
            boolean validTaskEvent = false;
            int i = 0;

            while (!validTaskEvent && i < parts.length) {
                if (TASK_PROJECT.equals(parts[i]) || TASK_SERVICE.equals(parts[i])) {
                    validTaskEvent = true;
                }

                i++;
            }

            if (validTaskEvent) {
                eventMetaData = new HashMap<>();
                eventMetaData.put(TASK_NAME, parts[3] +"." +parts[4]);
                eventMetaData.put(EVENT_TYPE, parts[5]);
            }
        }

        return eventMetaData;
    }

    /**
     * Checks if the given string array is as long as numOfParts and no entries are empty.
     * @param parts of the event type
     * @param numOfParts the check size of parts
     * @return true if the parts represent a valid event type or else false
     */
    private static boolean isValidEventType(String[] parts, int numOfParts) {
        boolean result = true;

        if (parts.length != numOfParts) {
            result = false;
        }

        if (result) {
            int i = 0;
            while(result && i < parts.length) {
                if ("".equals(parts[i])) {
                    result = false;
                }

                i++;
            }
        }

        return result;
    }

}
