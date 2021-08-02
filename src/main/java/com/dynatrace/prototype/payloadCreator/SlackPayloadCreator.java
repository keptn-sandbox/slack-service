package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.SLIEvaluationResult;
import com.dynatrace.prototype.domainModel.eventData.*;
import org.apache.maven.shared.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;

@ApplicationScoped
public class SlackPayloadCreator implements PayloadCreator {
    private static final String ENV_KEPTN_BRIDGE_DOMAIN = "KEPTN_BRIDGE_DOMAIN";

    @Override
    public String createPayload(KeptnCloudEvent event) {
        final String PROJECT = "project", STAGE = "stage", SERVICE = "service", STATUS = "status", RESULT = "result", MESSAGE = "message";
        StringBuilder sb = new StringBuilder();
        String eventType = event.getType();
        String eventName = StringUtils.capitalise(eventType.replace(KeptnEvent.SH_KEPTN_EVENT.getValue() +'.', ""));
        String eventProject = null;
        String eventStage = null;
        String eventService = null;
        String eventStatus = null;
        String eventResult = null;
        String eventMessage = null;
        String keptnBridgeDomain = System.getenv(ENV_KEPTN_BRIDGE_DOMAIN);
        String eventURL = "http://" + keptnBridgeDomain + "/bridge/dashboard";
        Object eventDataObject = event.getData();

        //TODO: improve the way how a slack block string is made out of the payload
        sb.append("[{\"type\": \"divider\"},{\"type\": \"section\",\"text\": {\"type\":\"mrkdwn\",\"text\": \"");

        eventName = StringUtils.reverse(StringUtils.reverse(eventName).replaceFirst("\\.", "_\n*"));
        sb.append('*').append(eventName).append("_\n").append(event.getSource()).append('\n');

        if (eventDataObject instanceof KeptnCloudEventData) {
            KeptnCloudEventData eventData = (KeptnCloudEventData) eventDataObject;

            eventProject = eventData.getProject();
            eventStage = eventData.getStage();
            eventService = eventData.getService();
            if (eventData.getStatus() != null) {
                eventStatus = eventData.getStatus().getValue();
            }
            if (eventData.getResult() != null) {
                eventResult = eventData.getResult().getValue();
            }
            eventMessage = eventData.getMessage();
        } else if (eventDataObject instanceof LinkedHashMap<?, ?>) {
            LinkedHashMap<String, ?> eventData = (LinkedHashMap<String, ?>) eventDataObject;

            eventProject = Objects.toString(eventData.get(PROJECT));
            eventStage = Objects.toString(eventData.get(STAGE));
            eventService = Objects.toString(eventData.get(SERVICE));
            eventStatus = Objects.toString(eventData.get(STATUS));
            eventResult = Objects.toString(eventData.get(RESULT));
            eventMessage = Objects.toString(eventData.get(MESSAGE));
        }

        if (eventProject != null) {
            eventURL = "http://" + keptnBridgeDomain + "/bridge/project/" + eventProject + "/sequence/" + event.getShkeptncontext() + "/event/" + event.getId();
        }
        sb.append(ifNotNull(null, formatLink(eventURL, "Keptn bridge"), "\n\n"));
        sb.append("\"}},{\"type\": \"divider\"},{\"type\": \"section\",\"text\": {\"type\":\"mrkdwn\",\"text\": \""); //TODO: must be changed later
        sb.append(ifNotNull("Project: ", eventProject, "\n"));
        sb.append(ifNotNull("Stage: ", eventStage, "\n"));
        sb.append(ifNotNull("Service: ", eventService, "\n"));
        sb.append(ifNotNull("Status: ", eventStatus, "\n"));
        sb.append(ifNotNull("Result: ", eventResult, "\n"));
        sb.append(ifNotNull("Message: ", eventMessage, "\n"));

        if (eventType.startsWith(KeptnEvent.PROJECT.getValue())) { //TODO: maybe only if event type is .finished because .triggered events have a different type of eventData and probably only .finished provides the information
            if (eventDataObject instanceof KeptnCloudEventProjectData) {
                KeptnCloudEventProjectData eventData = (KeptnCloudEventProjectData) eventDataObject;
                KeptnCloudEventProjectFinishedData createdProject = eventData.getCreatedProject();

                if (createdProject != null) {
                    sb.append(ifNotNull("Project name: ", createdProject.getProjectName(), "\n"));
                }
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventProjectData although the event type is \"Project\"! (maybe because it is a .triggered event)");
            }
        } else if (eventType.startsWith(KeptnEvent.SERVICE.getValue())) {

        } else if (eventType.startsWith(KeptnEvent.APPROVAL.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventApprovalData) {
                KeptnCloudEventApprovalData eventData = (KeptnCloudEventApprovalData) eventDataObject;

                sb.append(ifNotNull("Pass: ", eventData.getApprovalPass(), "\n"));
                sb.append(ifNotNull("Warning: ", eventData.getApprovalWarning(), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventApprovalData although the event type is \"Approval\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.DEPLOYMENT.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventDeploymentData) {
                KeptnCloudEventDeploymentData eventData = (KeptnCloudEventDeploymentData) eventDataObject;

                sb.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n"));
                sb.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n"));
                sb.append(ifNotNull("Deployment names: ", Objects.toString(eventData.getDeploymentNames()), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventDeploymentData although the event type is \"Deployment\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.TEST.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventTestData) {
                KeptnCloudEventTestData eventData = (KeptnCloudEventTestData) eventDataObject;

                sb.append(ifNotNull("Start: ", eventData.getTestStart(), "\n"));
                sb.append(ifNotNull("End: ", eventData.getTestEnd(), "\n"));
                sb.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n"));
                sb.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n"));
                sb.append(ifNotNull("Git commit: ", eventData.getTestGitCommit(), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventTestData although the event type is \"Test\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.EVALUATION.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventEvaluationData) {
                KeptnCloudEventEvaluationData eventData = (KeptnCloudEventEvaluationData) eventDataObject;

                sb.append(ifNotNull("Start: ", eventData.getEvaluationStart(), "\t"));
                sb.append(ifNotNull("End: ", eventData.getEvaluationEnd(), "\n"));
                sb.append(ifNotNull("Result: ", eventData.getEvaluationResult(), "\n"));
                sb.append(ifNotNull("Score: ", eventData.getEvaluationScore(), "\n"));

                HashSet<SLIEvaluationResult> sliResultSet = eventData.getSLIEvaluationResults();
                if (sliResultSet != null) {
                    Iterator<SLIEvaluationResult> sliResIterator = sliResultSet.iterator();

                    if (sliResIterator.hasNext()) {
                        sb.append("SLI:\n");
                        sb.append("Name\t|\t").append("Value\t|\t").append("pass Criteria\t|\t").append("warning Criteria\t|\t").append("Result\t|\t").append("Score\n");
                        while (sliResIterator.hasNext()) {
                            SLIEvaluationResult element = sliResIterator.next();
                            sb.append(element.getDisplayName()).append("\t|\t").append(element.getValue().getValue()).append("\t|\t").append(element.getPassTargets().toString()).append("\t|\t").append(element.getWarningTargets().toString()).append("\t|\t").append(element.getStatus()).append("\t|\t").append(element.getScore()).append("\n");
                        }
                    }
                }
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventEvaluationData although the event type is \"Evaluation\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.RELEASE.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventReleaseData) {
                KeptnCloudEventReleaseData eventData = (KeptnCloudEventReleaseData) eventDataObject;

                sb.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n"));
                sb.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n"));
                sb.append(ifNotNull("Deployment names: ", Objects.toString(eventData.getDeploymentNames()), "\n"));
            }
        } else if (eventType.startsWith(KeptnEvent.GET_ACTION.getValue()) || eventType.startsWith(KeptnEvent.ACTION.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventActionData) {
                KeptnCloudEventActionData eventData = (KeptnCloudEventActionData) eventDataObject;

                sb.append(ifNotNull("Problem title: ", eventData.getProblemTitle(), "\n"));
                sb.append(ifNotNull("Problem root cause: ", eventData.getProblemRootCause(), "\n"));
                sb.append(ifNotNull("Action name: ", eventData.getActionName(), "\n"));
                sb.append(ifNotNull("Action: ", eventData.getRealAction(), "\n"));
                sb.append(ifNotNull("Action description: ", eventData.getActionDescription(), "\n"));
                sb.append(ifNotNull("Additional action values: ", Objects.toString(eventData.getAdditionalActionValues()), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventActionData although the event type is \"Action / Get-Action\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.GET_SLI.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventGetSLIData) {
                KeptnCloudEventGetSLIData eventData = (KeptnCloudEventGetSLIData) eventDataObject;

                sb.append(ifNotNull("Sli Provider: ", eventData.getSliProvider(), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventGetSLIData although the event type is \"Get-Sli\"!");
            }
        } else if (eventType.contains("problem") || eventType.startsWith(KeptnEvent.PROBLEM.getValue())) { //TODO: check if the Problem event starts with .events or .event without "s"
            if (eventDataObject instanceof KeptnCloudEventProblemData) {
                KeptnCloudEventProblemData eventData = (KeptnCloudEventProblemData) eventDataObject;

                sb.append(ifNotNull("State: ", eventData.getState(), "\n"));
                sb.append(ifNotNull("Problem ID: ", eventData.getProblemID(), "\n"));
                sb.append(ifNotNull("Problem Title: ", eventData.getProblemTitle(), "\n"));
                sb.append(ifNotNull(null, formatLink(eventData.getProblemURL(), "Problem URL"), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventProblemData although the event type is \"Problem\"!");
            }
        } /*else if (eventType.startsWith(KeptnEvent.ROLLBACK.getValue())) {
        } */else {
            sb.append("Add default event information here");
        }

        sb.append("\"}},{\"type\": \"divider\"}]"); //TODO: must be changed later

        return sb.toString();
    }

    /**
     * Returns key +value if value is not null nor blank, else an empty String ("")
     * @param prefix to insert before value if not null
     * @param value to check
     * @param postfix to insert after value if not null
     * @return key +value or an empty String ("")
     */
    private String ifNotNull(String prefix, String value, String postfix) {
        String result = "";

        if (value != null && !value.isBlank() && !value.equals("null")) {
            if (prefix != null) {
                result = prefix;
            }
            result += value;
            if (postfix != null) {
                result += postfix;
            }
        }

        return result;
    }

    /**
     * Formats a given link to display only the displayText if send to Slack.
     * Returns the formatted link or null if the link is null.
     * @param link to format
     * @param displayText text which will displayed instead of the link.
     * @return formatted link or null
     */
    private String formatLink(String link, String displayText) {
        String formattedEmail = null;

        if (link != null) {
            formattedEmail = "<" +link +"|" +displayText +">";
        }

        return formattedEmail;
    }

}
