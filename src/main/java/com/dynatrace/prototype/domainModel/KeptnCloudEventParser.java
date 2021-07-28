package com.dynatrace.prototype.domainModel;

import com.dynatrace.prototype.domainModel.eventData.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public class KeptnCloudEventParser {

    public static KeptnCloudEvent parseJsonToKeptnCloudEvent(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper(); //TODO: maybe use only one mapper because it is thread-save
        KeptnCloudEvent result = objectMapper.readValue(jsonString, KeptnCloudEvent.class);

        if (parseDataPayload(result, jsonString)) {
            System.out.println("Parsed event data value successfully.");
        } else {
            System.out.println("WARN: Failed to pass event data value (general parsing provided).");
        }

        return result;
    }

    private static boolean parseDataPayload(KeptnCloudEvent event, String json) {
        boolean parsed = true;
        ObjectMapper mapper = new ObjectMapper();
        String eventType = event.getType();

        if (eventType.equals(KeptnEvent.PROJECT_TRIGGERED.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventProjectTriggeredData.class));
        } else if (eventType.startsWith(KeptnEvent.PROJECT.getValue())) { //would also accept project.triggered but that is filtered out above.
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventProjectData.class));
        } else if (eventType.startsWith(KeptnEvent.SERVICE.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventData.class));
        } else if (eventType.startsWith(KeptnEvent.APPROVAL.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventApprovalData.class));
        } else if (eventType.startsWith(KeptnEvent.DEPLOYMENT.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventDeploymentData.class));
        } else if (eventType.startsWith(KeptnEvent.TEST.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventTestData.class));
        } else if (eventType.startsWith(KeptnEvent.EVALUATION.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventEvaluationData.class));
        } else if (eventType.startsWith(KeptnEvent.RELEASE.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventReleaseData.class));
        } else if (eventType.startsWith(KeptnEvent.GET_ACTION.getValue()) || eventType.startsWith(KeptnEvent.ACTION.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventActionData.class));
        } else if (eventType.startsWith(KeptnEvent.GET_SLI.getValue())) {
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventGetSLIData.class));
        } else if (eventType.contains("problem") || KeptnEvent.PROBLEM.getValue().equals(eventType)) { //TODO: check if Problem events start with sh.keptn.events or without the "s"
            event.setData(mapper.convertValue(event.getData(), KeptnCloudEventProblemData.class));
        } /*else if (eventType.startsWith(KeptnEvent.ROLLBACK.getValue())) {
            //event.setData(mapper.convertValue(event.getData(), KeptnCloudEventRollbackData.class)); TODO: Rollback class
        } */else {
            parsed = false;
        }

        return parsed;
    }
}
