package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventApprovalData;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventApproval;
import com.google.gson.Gson;
import com.slack.api.model.block.ActionsBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.ConfirmationDialogObject;
import com.slack.api.model.block.element.BlockElement;
import org.apache.maven.shared.utils.StringUtils;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class ApprovalMapper extends KeptnCloudEventMapper {
    private static final String eventName = KeptnEvent.APPROVAL.getValue();
    private static final Logger LOG = Logger.getLogger(ApprovalMapper.class);
    private static final Gson GSON = new Gson();

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

        if (event instanceof KeptnCloudEventApproval) {
            layoutBlockList.addAll(getApprovalData((KeptnCloudEventApproval) event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getApprovalData(KeptnCloudEventApproval event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        KeptnCloudEventApprovalData eventData = event.getData();

        if (eventData != null) {
            StringBuilder message = new StringBuilder();
            boolean manual = false;
            String pass = eventData.getApprovalPass();
            String warning = eventData.getApprovalWarning();
            KeptnCloudEventDataResult result = eventData.getResult();
            String eventType = event.getPlainEventType();
            String service = eventData.getService();
            String stage = eventData.getStage();
            String project = eventData.getProject();

            if (!logErrorIfNull(stage, "Stage", eventName) &&
                    !logErrorIfNull(service, "Service", eventName) &&
                    !logErrorIfNull(project, "Project", eventName)) {

                if (KeptnEvent.TRIGGERED.getValue().equals(eventType)) {
                    if (KeptnCloudEventDataResult.PASS.equals(result) && MANUAL.equals(pass) ||
                            KeptnCloudEventDataResult.WARNING.equals(result) && MANUAL.equals(warning)) {
                        message.append(String.format("Do you want to promote " + SERVICE_STAGE_PROJECT_TEXT + "?", service, stage, project));
                        manual = true;
                    } else if (KeptnCloudEventDataResult.FAIL.equals(result)) {
                        message.append(String.format("There was an error when approving " + SERVICE_STAGE_PROJECT_TEXT + ".", service, stage, project));
                    } else {
                        message.append(String.format(StringUtils.capitalise(SERVICE_STAGE_PROJECT_TEXT) + " was promoted.", service, stage, project));
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
        }

        return layoutBlockList;
    }

    private LayoutBlock createApprovalButtons(KeptnCloudEventApproval event) {
        List<BlockElement> buttons = new ArrayList<>();
        ConfirmationDialogObject confirmationApprove = SlackCreator.createConfirmationDialog(CONFIRM_TITLE,
                String.format(CONFIRM_TEXT, APPROVAL_APPROVE_VALUE), CONFIRM_YES, CONFIRM_NO,
                SlackCreator.SLACK_STYLE_PRIMARY);
        ConfirmationDialogObject confirmationDeny = SlackCreator.createConfirmationDialog(CONFIRM_TITLE,
                String.format(CONFIRM_TEXT, APPROVAL_DENY_VALUE), CONFIRM_YES, CONFIRM_NO,
                SlackCreator.SLACK_STYLE_DANGER);

        System.out.println(GSON.toJson(event));

        //TODO: maybe improve how the event is sent to make the payload smaller
        buttons.add(SlackCreator.createButton(APPROVAL_APPROVE_ID, APPROVAL_APPROVE_VALUE,
                GSON.toJson(event), SlackCreator.SLACK_STYLE_PRIMARY, confirmationApprove));
        buttons.add(SlackCreator.createButton(APPROVAL_DENY_ID, APPROVAL_DENY_VALUE, null,
                SlackCreator.SLACK_STYLE_DANGER, confirmationDeny));

        return SlackCreator.createLayoutBlock(ActionsBlock.TYPE, buttons);
    }
}
