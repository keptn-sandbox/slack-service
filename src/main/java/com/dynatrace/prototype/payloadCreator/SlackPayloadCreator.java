package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.SLIEvaluationResult;
import com.dynatrace.prototype.domainModel.eventData.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.model.block.*;
import com.slack.api.model.block.composition.PlainTextObject;
import org.apache.maven.shared.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

import static com.slack.api.model.block.composition.BlockCompositions.markdownText;

@ApplicationScoped
public class SlackPayloadCreator {
    private static final String ENV_KEPTN_BRIDGE_DOMAIN = "KEPTN_BRIDGE_DOMAIN";

    public List<LayoutBlock> createPayload(KeptnCloudEvent event) {
        final String PROJECT = "project";
        final String STAGE = "stage";
        final String SERVICE = "service";
        final String STATUS = "status";
        final String RESULT = "result";
        final String MESSAGE = "message";
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        String eventType = event.getType();
        String eventName = StringUtils.capitalise(eventType.replace(KeptnEvent.SH_KEPTN_EVENT.getValue() +'.', ""));
        String eventProject = null;
        String eventStage = null;
        String eventService = null;
        String eventStatus = null;
        String eventResult = null;
        String eventMessage = null;
        String keptnBridgeDomain = System.getenv(ENV_KEPTN_BRIDGE_DOMAIN); //TODO: handle if null
        String eventURL = "http://" + keptnBridgeDomain + "/bridge/dashboard";
        Object eventDataObject = event.getData();

        eventName = StringUtils.reverse(StringUtils.reverse(eventName).replaceFirst("\\.", " - "));
        layoutBlockList.add(createSlackBlock(HeaderBlock.TYPE, ifNotNull(null, eventName, null)));

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
        layoutBlockList.add(createSlackBlock(SectionBlock.TYPE,
                ifNotNull(null, event.getSource(), "\n")
                        +ifNotNull(null, formatLink(eventURL, "Keptn bridge"), "\n\n")));
        layoutBlockList.add(createSlackBlock(DividerBlock.TYPE));
        layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, "```"
                        +ifNotNull(String.format("%-20s", "Project: "), eventProject, "\n")
                        +ifNotNull(String.format("%-20s", "Stage: "), eventStage, "\n")
                        +ifNotNull(String.format("%-20s", "Service: "), eventService, "\n")
                        +ifNotNull(String.format("%-20s", "Status: "), eventStatus, "\n")
                        +ifNotNull(String.format("%-20s", "Result: "), eventResult, "\n")
                        +ifNotNull(String.format("%-20s", "Message: "), eventMessage, "\n")
                        +"```"));

        StringBuilder eventDataSB = new StringBuilder();
        if (eventType.startsWith(KeptnEvent.PROJECT.getValue())) { //TODO: maybe only if event type is .finished because .triggered events have a different type of eventData and probably only .finished provides the information
            if (eventDataObject instanceof KeptnCloudEventProjectData) {
                KeptnCloudEventProjectData eventData = (KeptnCloudEventProjectData) eventDataObject;
                KeptnCloudEventProjectFinishedData createdProject = eventData.getCreatedProject();

                if (createdProject != null) {
                    eventDataSB.append(ifNotNull("Project name: ", createdProject.getProjectName(), "\n"));
                }
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventProjectData although the event type is \"Project\"! (maybe because it is a .triggered event)");
            }
        } else if (eventType.startsWith(KeptnEvent.SERVICE.getValue())) {

        } else if (eventType.startsWith(KeptnEvent.APPROVAL.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventApprovalData) {
                KeptnCloudEventApprovalData eventData = (KeptnCloudEventApprovalData) eventDataObject;

                eventDataSB.append(ifNotNull("Pass: ", eventData.getApprovalPass(), "\n"));
                eventDataSB.append(ifNotNull("Warning: ", eventData.getApprovalWarning(), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventApprovalData although the event type is \"Approval\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.DEPLOYMENT.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventDeploymentData) {
                KeptnCloudEventDeploymentData eventData = (KeptnCloudEventDeploymentData) eventDataObject;

                eventDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n"));
                eventDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n"));
                eventDataSB.append(ifNotNull("Deployment names: ", Objects.toString(eventData.getDeploymentNames()), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventDeploymentData although the event type is \"Deployment\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.TEST.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventTestData) {
                KeptnCloudEventTestData eventData = (KeptnCloudEventTestData) eventDataObject;

                eventDataSB.append(ifNotNull("Start: ", eventData.getTestStart(), "\n"));
                eventDataSB.append(ifNotNull("End: ", eventData.getTestEnd(), "\n"));
                eventDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n"));
                eventDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n"));
                eventDataSB.append(ifNotNull("Git commit: ", eventData.getTestGitCommit(), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventTestData although the event type is \"Test\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.EVALUATION.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventEvaluationData) {
                KeptnCloudEventEvaluationData eventData = (KeptnCloudEventEvaluationData) eventDataObject;

                eventDataSB.append(ifNotNull("Start: ", eventData.getEvaluationStart(), "\t"));
                eventDataSB.append(ifNotNull("End: ", eventData.getEvaluationEnd(), "\n"));
                eventDataSB.append(ifNotNull("Result: ", eventData.getEvaluationResult(), "\n"));
                eventDataSB.append(ifNotNull("Score: ", eventData.getEvaluationScore(), "\n"));

                HashSet<SLIEvaluationResult> sliResultSet = eventData.getSLIEvaluationResults();
                if (sliResultSet != null) {
                    Iterator<SLIEvaluationResult> sliResIterator = sliResultSet.iterator();

                    if (sliResIterator.hasNext()) {
                        eventDataSB.append("SLI:\n");
                        eventDataSB.append(String.format("%-20s|", "Name")).append(String.format("%-20s|", "Value")).append(String.format("%-20s|", "pass Criteria")).append(String.format("%-20s|", "warning Criteria")).append(String.format("%-20s|", "Result")).append("Score\n");
                        while (sliResIterator.hasNext()) {
                            SLIEvaluationResult element = sliResIterator.next();
                            eventDataSB.append(element.getDisplayName()).append("\t|\t").append(element.getValue().getValue()).append("\t|\t").append(element.getPassTargets().toString()).append("\t|\t").append(element.getWarningTargets().toString()).append("\t|\t").append(element.getStatus()).append("\t|\t").append(element.getScore()).append("\n");
                        }
                    }
                }
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventEvaluationData although the event type is \"Evaluation\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.RELEASE.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventReleaseData) {
                KeptnCloudEventReleaseData eventData = (KeptnCloudEventReleaseData) eventDataObject;

                eventDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n"));
                eventDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n"));
                eventDataSB.append(ifNotNull("Deployment names: ", Objects.toString(eventData.getDeploymentNames()), "\n"));
            }
        } else if (eventType.startsWith(KeptnEvent.GET_ACTION.getValue()) || eventType.startsWith(KeptnEvent.ACTION.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventActionData) {
                KeptnCloudEventActionData eventData = (KeptnCloudEventActionData) eventDataObject;

                eventDataSB.append(ifNotNull("Problem title: ", eventData.getProblemTitle(), "\n"));
                eventDataSB.append(ifNotNull("Problem root cause: ", eventData.getProblemRootCause(), "\n"));
                eventDataSB.append(ifNotNull("Action name: ", eventData.getActionName(), "\n"));
                eventDataSB.append(ifNotNull("Action: ", eventData.getRealAction(), "\n"));
                eventDataSB.append(ifNotNull("Action description: ", eventData.getActionDescription(), "\n"));
                eventDataSB.append(ifNotNull("Additional action values: ", Objects.toString(eventData.getAdditionalActionValues()), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventActionData although the event type is \"Action / Get-Action\"!");
            }
        } else if (eventType.startsWith(KeptnEvent.GET_SLI.getValue())) {
            if (eventDataObject instanceof KeptnCloudEventGetSLIData) {
                KeptnCloudEventGetSLIData eventData = (KeptnCloudEventGetSLIData) eventDataObject;

                eventDataSB.append(ifNotNull("Sli Provider: ", eventData.getSliProvider(), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventGetSLIData although the event type is \"Get-Sli\"!");
            }
        } else if (eventType.contains("problem") || eventType.startsWith(KeptnEvent.PROBLEM.getValue())) { //TODO: check if the Problem event starts with .events or .event without "s"
            if (eventDataObject instanceof KeptnCloudEventProblemData) {
                KeptnCloudEventProblemData eventData = (KeptnCloudEventProblemData) eventDataObject;

                eventDataSB.append(ifNotNull("State: ", eventData.getState(), "\n"));
                eventDataSB.append(ifNotNull("Problem ID: ", eventData.getProblemID(), "\n"));
                eventDataSB.append(ifNotNull("Problem Title: ", eventData.getProblemTitle(), "\n"));
                eventDataSB.append(ifNotNull(null, formatLink(eventData.getProblemURL(), "Problem URL"), "\n"));
            } else {
                System.out.println("WARN: eventData is not an instance of KeptnCloudEventProblemData although the event type is \"Problem\"!");
            }
        } /*else if (eventType.startsWith(KeptnEvent.ROLLBACK.getValue())) {
        } */else {
            eventDataSB.append("Add default event information here");
        }

        if (eventDataSB.length() > 0) {
            layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, "```" +eventDataSB +"```"));
        }
        layoutBlockList.add(createSlackBlock(DividerBlock.TYPE));

        return layoutBlockList;
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

    /**
     * This method creates a slack block with the given type (if such a block exists).
     * Payload is the text of the block  if it needs one else it will be ignored.
     * @param type of the text (e.g. SectionBlock.TYPE). supported are: SectionBlock, HeaderBlock, DividerBlock
     * @param payload the text if the slack block requires a text
     * @return the specific slack block or null
     */
    private LayoutBlock createSlackBlock(String type, String payload) {
        LayoutBlock slackBlock = null;

        if (SectionBlock.TYPE.equals(type)) {
            slackBlock = Blocks.section(section -> section.text(markdownText(payload)));
        } else if (HeaderBlock.TYPE.equals(type)) {
            slackBlock = HeaderBlock.builder().text(PlainTextObject.builder().text(payload).build()).build();
        } else if (DividerBlock.TYPE.equals(type)) {
            slackBlock = DividerBlock.builder().build();
        }
        //TODO: maybe add an ActionBlock (if needed)

        return slackBlock;
    }

    /**
     * This method creates a slack block with the given type (if such a block exists).
     * @param type of the text (e.g. SectionBlock.TYPE). supported are: SectionBlock, HeaderBlock, DividerBlock
     * @return the specific slack block or null
     */
    private LayoutBlock createSlackBlock(String type) {
        return createSlackBlock(type, null);
    }

    /**
     * Parses the given LayoutBlockList to a JSON string.
     * Ignores null values.
     * Returns a JSON string representing the given list.
     * @param blockList to parse
     * @return JSON if successful or else null
     */
    private String parseBlockListToJSON(List<LayoutBlock> blockList) {
        String blockListString = null;
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            blockListString = mapper.writeValueAsString(blockList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return blockListString;
    }

    /**
     * Parses the given LayoutBlock to a JSON string.
     * Ignores null values.
     * Returns a JSON string representing the given layoutBlock.
     * @param block to parsed
     * @return JSON if successful or els null
     */
    private String parseToSlackBlockString(LayoutBlock block) {
        String blockString = null;
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            blockString = mapper.writeValueAsString(block);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return blockString;
    }
}
