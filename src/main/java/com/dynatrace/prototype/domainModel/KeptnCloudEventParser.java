package com.dynatrace.prototype.domainModel;

import com.dynatrace.prototype.domainModel.eventData.*;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventDefault;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.*;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventType;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jboss.logging.Logger;

import java.util.HashMap;

public class KeptnCloudEventParser {
    private static final Logger LOG = Logger.getLogger(KeptnCloudEventParser.class);
    private static final Gson GSON = new Gson();

    public static KeptnCloudEvent parseJsonToKeptnCloudEvent(String jsonString) throws JsonSyntaxException {
        KeptnCloudEventType fullEventType = GSON.fromJson(jsonString, KeptnCloudEventType.class);
        KeptnCloudEvent result = null;
        HashMap<String, String> eventMetaData = KeptnCloudEventValidator.parseSequenceEventType(fullEventType.getType());

        if (eventMetaData != null) {
            result = GSON.fromJson(jsonString, KeptnCloudEventDefault.class);
        } else {
            eventMetaData = KeptnCloudEventValidator.parseTaskEventType(fullEventType.getType());

            if (eventMetaData == null) {
                LOG.error("Cannot parse the given JSON to a KeptnCloudEvent!");
            } else {
                String taskName = eventMetaData.get(KeptnCloudEventValidator.TASK_NAME);

                if (KeptnEvent.PROJECT.getValue().equals(taskName)) {
                    String eventType = eventMetaData.get((KeptnCloudEventValidator.EVENT_TYPE));

                    if (KeptnEvent.TRIGGERED.getValue().equals(eventType)){
                        result = GSON.fromJson(jsonString, KeptnCloudEventProjectTriggered.class);
                    } else {
                        result = GSON.fromJson(jsonString, KeptnCloudEventProject.class);
                    }
                } else if (KeptnEvent.SERVICE.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventDefault.class);
                } else if (KeptnEvent.APPROVAL.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventApproval.class);
                } else if (KeptnEvent.DEPLOYMENT.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventDeployment.class);
                } else if (KeptnEvent.TEST.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventTest.class);
                } else if (KeptnEvent.EVALUATION.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventEvaluation.class);
                } else if (KeptnEvent.RELEASE.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventRelease.class);
                } else if (KeptnEvent.GET_ACTION.getValue().equals(taskName) || KeptnEvent.ACTION.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventAction.class);
                } else if (KeptnEvent.GET_SLI.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventGetSLI.class);
                } else if (KeptnEvent.PROBLEM.getValue().equals(taskName)) {
                    result = GSON.fromJson(jsonString, KeptnCloudEventProblem.class);
                } /*else if (KeptnEvent.ROLLBACK.getValue().equals(taskName)) {
                    //result = GSON.fromJson(jsonString, KeptnCloudEventRollback.class); TODO: Rollback class
                } */
            }
        }

        return result;
    }

}
