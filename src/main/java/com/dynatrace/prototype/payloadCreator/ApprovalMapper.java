package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventApprovalData;
<<<<<<< HEAD
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
=======
>>>>>>> aa0befd (change the message of the approval event)
import com.slack.api.model.block.ActionsBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.ConfirmationDialogObject;
import com.slack.api.model.block.element.BlockElement;

import java.util.ArrayList;
import java.util.List;

public class ApprovalMapper extends KeptnCloudEventMapper {
    private static final String MANUAL = "manual";
    private static final String AUTOMATIC = "automatic";

    private static final String CONFIRM_TITLE = "Are you sure?";
    private static final String CONFIRM_TEXT = "Are you sure that you want to %s it?";
    private static final String CONFIRM_YES = "yes";
    private static final String CONFIRM_NO = "cancel";
<<<<<<< HEAD
    public static final String APPROVAL_APPROVE_VALUE = "approve";
    public static final String APPROVAL_DENY_VALUE = "deny";
    public static final String APPROVAL_APPROVE_ID = "approve";
    public static final String APPROVAL_DENY_ID = "deny";
=======
    private static final String APPROVAL_APPROVE = "approve";
    private static final String APPROVAL_DENY = "deny";
>>>>>>> aa0befd (change the message of the approval event)

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.APPROVAL.getValue())) {
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
            KeptnCloudEventDataResult result = eventData.getResult();
            String pass = eventData.getApprovalPass();
            String warning = eventData.getApprovalWarning();
            String service = eventData.getService();
            String project = eventData.getProject();
            String stage = eventData.getStage();

            if (stage == null) {
                System.err.println("Stage of approval is null!");
            } else if (service == null) {
                System.err.println("Service of approval is null!");
            } else if (project == null) {
                System.err.println("Project of approval is null!");
            } else {
<<<<<<< HEAD
                if (KeptnCloudEventDataResult.PASS.equals(result) && MANUAL.equals(pass) ||
                        KeptnCloudEventDataResult.WARNING.equals(result) && MANUAL.equals(warning)) {
                    message.append(String.format("Do you want to promote from stage '%1$s' the service '%2$s' of the project '%3$s'?", stage, service, project));
                    manual = true;
                } else if (KeptnCloudEventDataResult.FAIL.equals(result)) {
                    message.append(String.format("There was an error when approving the service '%1$s' from stage '%2$s' of the project '%3$s'.", service, stage, project));
                } else {
=======
                if (KeptnCloudEventDataResult.PASS.getValue().equals(result.getValue()) && MANUAL.equals(pass) ||
                        KeptnCloudEventDataResult.WARNING.getValue().equals(result.getValue()) && MANUAL.equals(warning)) {
                    message.append(String.format("Do you want to promote from stage '%1$s' the service '%2$s' of the project '%3$s'?", stage, service, project));
                    manual = true;
                } else {
                    //TODO: what should I do if the result is "fail"?
>>>>>>> aa0befd (change the message of the approval event)
                    message.append(String.format("The service '%1$s' from stage '%2$s' of the project '%3$s' was promoted.", service, stage, project));
                }
            }

            if (message.length() > 0) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, message.toString()));
                if (manual) {
                    List<BlockElement> buttons = new ArrayList<>();
<<<<<<< HEAD
                    ConfirmationDialogObject confirmationApprove = createSlackConfirmationDialog(CONFIRM_TITLE, String.format(CONFIRM_TEXT, APPROVAL_APPROVE_VALUE), CONFIRM_YES, CONFIRM_NO, SLACK_STYLE_PRIMARY);
                    ConfirmationDialogObject confirmationDeny = createSlackConfirmationDialog(CONFIRM_TITLE, String.format(CONFIRM_TEXT, APPROVAL_DENY_VALUE), CONFIRM_YES, CONFIRM_NO, SLACK_STYLE_DANGER);

                    try {
                        //TODO: maybe improve how the event is sent to make the payload smaller
                        ObjectMapper mapper = new ObjectMapper();

                        buttons.add(createSlackButton(APPROVAL_APPROVE_ID, APPROVAL_APPROVE_VALUE, mapper.writeValueAsString(event), SLACK_STYLE_PRIMARY, confirmationApprove));
                        buttons.add(createSlackButton(APPROVAL_DENY_ID, APPROVAL_DENY_VALUE, null, SLACK_STYLE_DANGER, confirmationDeny));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
=======
                    ConfirmationDialogObject confirmationApprove = createSlackConfirmationDialog(CONFIRM_TITLE, String.format(CONFIRM_TEXT, APPROVAL_APPROVE), CONFIRM_YES, CONFIRM_NO, SLACK_STYLE_PRIMARY);
                    ConfirmationDialogObject confirmationDeny = createSlackConfirmationDialog(CONFIRM_TITLE, String.format(CONFIRM_TEXT, APPROVAL_DENY), CONFIRM_YES, CONFIRM_NO, SLACK_STYLE_DANGER);

                    buttons.add(createSlackButton(APPROVAL_APPROVE, "yes", SLACK_STYLE_PRIMARY, confirmationApprove));
                    buttons.add(createSlackButton(APPROVAL_DENY, "no", SLACK_STYLE_DANGER, confirmationDeny));
>>>>>>> aa0befd (change the message of the approval event)

                    layoutBlockList.add(createSlackBlock(ActionsBlock.TYPE, buttons));
                }
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventApprovalData although the event type is \"Approval\"!");
        }

        return layoutBlockList;
    }
}
