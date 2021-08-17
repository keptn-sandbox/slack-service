package com.dynatrace.prototype.domainModel;

import com.dynatrace.prototype.domainModel.eventData.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class KeptnCloudEventParser {

    public static KeptnCloudEvent parseJsonToKeptnCloudEvent(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper(); //TODO: maybe use only one mapper because it is thread-save
        KeptnCloudEvent result = objectMapper.readValue(jsonString, KeptnCloudEvent.class);

        if (parseDataPayload(result)) {
            System.out.println("Parsed event data value successfully.");
        } else {
            System.out.println("WARN: Failed to pass event data value (general parsing provided).");
        }

        return result;
    }

    private static boolean parseDataPayload(KeptnCloudEvent event) {
        boolean parsed = true;
        ObjectMapper mapper = new ObjectMapper();
        String fullEventType = event.getFullEventType();
        HashMap<String, String> eventMetaData = KeptnCloudEventValidator.parseSequenceEventType(fullEventType);

        if (eventMetaData != null) {
            //TODO: parse sequence event type
        } else {
            eventMetaData = KeptnCloudEventValidator.parseTaskEventType(fullEventType);

            if (eventMetaData != null) {
                String taskName = eventMetaData.get(KeptnCloudEventValidator.TASK_NAME);

                if (KeptnEvent.PROJECT.getValue().equals(taskName)) {
                    String eventType = eventMetaData.get((KeptnCloudEventValidator.EVENT_TYPE));

                    if (KeptnEvent.TRIGGERED.getValue().equals(eventType)){
                        event.setData(mapper.convertValue(event.getData(), KeptnCloudEventProjectTriggeredData.class));
                    } else {
                        event.setData(mapper.convertValue(event.getData(), KeptnCloudEventProjectData.class));
                    }
                } else if (KeptnEvent.SERVICE.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventData.class));
                } else if (KeptnEvent.APPROVAL.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventApprovalData.class));
                } else if (KeptnEvent.DEPLOYMENT.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventDeploymentData.class));
                } else if (KeptnEvent.TEST.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventTestData.class));
                } else if (KeptnEvent.EVALUATION.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventEvaluationData.class));
                } else if (KeptnEvent.RELEASE.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventReleaseData.class));
                } else if (KeptnEvent.GET_ACTION.getValue().equals(taskName) || KeptnEvent.ACTION.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventActionData.class));
                } else if (KeptnEvent.GET_SLI.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventGetSLIData.class));
                } else if (KeptnEvent.PROBLEM.getValue().equals(taskName)) {
                    event.setData(mapper.convertValue(event.getData(), KeptnCloudEventProblemData.class));
                } /*else if (KeptnEvent.ROLLBACK.getValue().equals(taskName)) {
                    //event.setData(mapper.convertValue(event.getData(), KeptnCloudEventRollbackData.class)); TODO: Rollback class
                } */else {
                    parsed = false;
                }
            }

            event.setMetaData(eventMetaData);
        }

        return parsed;
    }
}
