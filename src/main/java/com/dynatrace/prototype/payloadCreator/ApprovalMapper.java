package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventApprovalData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.model.block.ActionsBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.ConfirmationDialogObject;
import com.slack.api.model.block.element.BlockElement;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ApprovalMapper extends KeptnCloudEventMapper {
    private static final KeptnEvent eventName = KeptnEvent.APPROVAL;

    private static final String MANUAL = "manual";
    private static final String AUTOMATIC = "automatic";

    private static final String CONFIRM_TITLE = "Are you sure?";
    private static final String CONFIRM_TEXT = "Are you sure that you want to %s it?";
    private static final String CONFIRM_YES = "yes";
    private static final String CONFIRM_NO = "cancel";
    public static final String APPROVAL_APPROVE_VALUE = "approve";
    public static final String APPROVAL_DENY_VALUE = "deny";
    public static final String APPROVAL_APPROVE_ID = "approve";
    public static final String APPROVAL_DENY_ID = "deny";

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (eventName.getValue().equals(event.getTaskName())) {
            layoutBlockList.addAll(getApprovalData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getApprovalData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventApprovalData) {
            KeptnCloudEventApprovalData eventData = (KeptnCloudEventApprovalData) eventDataObject;
            StringBuilder message = new StringBuilder();
            boolean manual = false;
            String pass = eventData.getApprovalPass();
            String warning = eventData.getApprovalWarning();
            KeptnCloudEventDataResult result = eventData.getResult();
            String eventType = event.getPlainEventType();
            String service = eventData.getService();
            String stage = eventData.getStage();
            String project = eventData.getProject();

            if (stage == null) {
                System.err.printf(ERROR_NULL_VALUE, "Stage", eventName);
            } else if (service == null) {
                System.err.printf(ERROR_NULL_VALUE, "Service", eventName);
            } else if (project == null) {
                System.err.printf(ERROR_NULL_VALUE, "Project", eventName);
            } else {
                if (KeptnEvent.TRIGGERED.getValue().equals(eventType)) {
                    if (KeptnCloudEventDataResult.PASS.equals(result) && MANUAL.equals(pass) ||
                            KeptnCloudEventDataResult.WARNING.equals(result) && MANUAL.equals(warning)) {
                        message.append(String.format("Do you want to promote " + SERVICE_STAGE_PROJECT_TEXT +"?", service, stage, project));
                        manual = true;
                    } else if (KeptnCloudEventDataResult.FAIL.equals(result)) {
                        message.append(String.format("There was an error when approving " + SERVICE_STAGE_PROJECT_TEXT +".", service, stage, project));
                    } else {
                        message.append(String.format(StringUtils.capitalise(SERVICE_STAGE_PROJECT_TEXT) +" was promoted.", service, stage, project));
                    }
                } else if (KeptnEvent.STARTED.getValue().equals(eventType)) {
                    message.append(createMessage(result, KeptnEvent.STARTED, eventName, service, stage, project));
                } else if (KeptnEvent.FINISHED.getValue().equals(eventType)) {
                    message.append(createMessage(result, KeptnEvent.FINISHED, eventName, service, stage, project));
                }
            }

            if (message.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, message.toString()));
                if (manual) {
                    layoutBlockList.add(createApprovalButtons(event));
                }
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventApprovalData although the event type is \"Approval\"!");
        }

        return layoutBlockList;
    }

    private LayoutBlock createApprovalButtons(KeptnCloudEvent event) {
        List<BlockElement> buttons = new ArrayList<>();
        ConfirmationDialogObject confirmationApprove = SlackCreator.createConfirmationDialog(CONFIRM_TITLE,
                String.format(CONFIRM_TEXT, APPROVAL_APPROVE_VALUE), CONFIRM_YES, CONFIRM_NO,
                SlackCreator.SLACK_STYLE_PRIMARY);
        ConfirmationDialogObject confirmationDeny = SlackCreator.createConfirmationDialog(CONFIRM_TITLE,
                String.format(CONFIRM_TEXT, APPROVAL_DENY_VALUE), CONFIRM_YES, CONFIRM_NO,
                SlackCreator.SLACK_STYLE_DANGER);

        try {
            //TODO: maybe improve how the event is sent to make the payload smaller
            ObjectMapper mapper = new ObjectMapper();

            buttons.add(SlackCreator.createButton(APPROVAL_APPROVE_ID, APPROVAL_APPROVE_VALUE,
                    mapper.writeValueAsString(event), SlackCreator.SLACK_STYLE_PRIMARY, confirmationApprove));
            buttons.add(SlackCreator.createButton(APPROVAL_DENY_ID, APPROVAL_DENY_VALUE, null,
                    SlackCreator.SLACK_STYLE_DANGER, confirmationDeny));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return SlackCreator.createLayoutBlock(ActionsBlock.TYPE, buttons);
    }
}
