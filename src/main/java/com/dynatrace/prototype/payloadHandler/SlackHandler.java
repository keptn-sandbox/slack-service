package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.ApprovalService;
import com.dynatrace.prototype.domainModel.*;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventApprovalData;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.dynatrace.prototype.payloadCreator.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.chat.ChatUpdateRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.chat.ChatUpdateResponse;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.ActionsBlock;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.ButtonElement;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ApplicationScoped
public class SlackHandler implements KeptnCloudEventHandler {
    private static final String ENV_SLACK_TOKEN = "SLACK_TOKEN";
    private static final String ENV_SLACK_CHANNEL = "SLACK_CHANNEL";
    private static final String SLACK_NOTIFICATION_MSG = "A new Keptn Event arrived.";
    private static final String COLOR_PASS = "#00FF00";
    private static final String COLOR_WARNING = "#FFFF00";
    private static final String COLOR_FAIL = "#FF0000";

    private LinkedHashSet<KeptnCloudEventMapper> mappers;

    @Inject
    @RestClient
    ApprovalService approvalService;

    public SlackHandler() {
        this.mappers = new LinkedHashSet<>();

        mappers.add(new GeneralEventMapper());
        mappers.add(new ProjectMapper());
        mappers.add(new ServiceMapper());
        mappers.add(new ApprovalMapper());
        mappers.add(new DeploymentMapper());
        mappers.add(new TestMapper());
        mappers.add(new EvaluationMapper());
        mappers.add(new ReleaseMapper());
        mappers.add(new GetActionMapper());
        mappers.add(new GetSLIMapper());
        mappers.add(new ProblemMapper());
    }

    @Override
    public boolean receiveEvent(KeptnCloudEvent event) {
        boolean successful = false;
        Slack slack = Slack.getInstance();
        String token = System.getenv(ENV_SLACK_TOKEN);
        String channel = System.getenv(ENV_SLACK_CHANNEL);

        if (token == null) {
            System.err.println(ENV_SLACK_TOKEN + " is null!");
        } else if (channel == null) {
            System.err.println(ENV_SLACK_CHANNEL + " is null!");
        } else {
            try {
                List<LayoutBlock> layoutBlockList = new ArrayList<>();

                for (KeptnCloudEventMapper mapper : mappers) {
                    layoutBlockList.addAll(mapper.getSpecificData(event));
                }

                List<Attachment> attachments = SlackCreator.createAttachment(event, layoutBlockList, SLACK_NOTIFICATION_MSG);
                ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                        .channel(channel)
                        .attachments(attachments)
                        .build();
                ChatPostMessageResponse response = slack.methods(token).chatPostMessage(request);

                if (response.isOk()) {
                    successful = true;
                } else {
                    System.err.println("PAYLOAD_ERROR: " + response.getError());
                }
            } catch (IOException | SlackApiException e) {
                System.err.println("EXCEPTION: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return successful;
    }

    public boolean sendEvent(Object payload) {
        boolean successful = false;

        Slack slack = Slack.getInstance();
        String token = System.getenv(ENV_SLACK_TOKEN);

        if (payload instanceof BlockActionPayload) {
            BlockActionPayload actionPayload = (BlockActionPayload) payload;

            ChatUpdateRequest updateRequest = ChatUpdateRequest.builder()
                    .channel(actionPayload.getChannel().getId())
                    .ts(actionPayload.getMessage().getTs())
                    .attachments(createUpdateMessage(actionPayload))
                    .build();

            try {
                ChatUpdateResponse response = slack.methods(token).chatUpdate(updateRequest);

                if (response.isOk()) {
                    successful = true;
                } else {
                    System.err.println("PAYLOAD_ERROR: " + response.getError());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SlackApiException e) {
                e.printStackTrace();
            }

        }

        return successful;
    }

    /**
     * Create an attachment list with one attachment.
     * The blocks of it are the updated slack message blocks.
     *
     * @param payload of the button click
     * @return a list with one attachment if successful or else null
     */
    private List<Attachment> createUpdateMessage(BlockActionPayload payload) {
        List<Attachment> attachments = null;
        boolean sendEventError = false;

        if (payload.getMessage() != null && payload.getMessage().getAttachments() != null) {
            attachments = payload.getMessage().getAttachments();

            Attachment firstAttachment = payload.getMessage().getAttachments().get(0);
            List<LayoutBlock> newBlocks = new ArrayList<>();
            KeptnCloudEventDataResult approvalFinishedResult = null;
            KeptnCloudEvent approvalTriggered = null;

            if (firstAttachment != null && firstAttachment.getBlocks() != null) {
                Iterator<LayoutBlock> oldBlocksIterator = firstAttachment.getBlocks().iterator();
                boolean foundDivider = false;
                ActionsBlock firstActions = null; //The action containing a button with the Keptn event as value

                while (!(foundDivider && firstActions != null) && oldBlocksIterator.hasNext()) {
                    LayoutBlock current = oldBlocksIterator.next();

                    if (!foundDivider) {
                        newBlocks.add(current);
                        if (current instanceof DividerBlock) {
                            foundDivider = true;
                        }
                    }

                    if (firstActions == null && current instanceof ActionsBlock) {
                        firstActions = (ActionsBlock) current;
                    }
                }

                approvalTriggered = extractKeptnCloudEvent(firstActions);
            }

            if (payload.getActions() != null) {
                BlockActionPayload.Action buttonAction = payload.getActions().get(0);
                String updateMsg = null;

                if (buttonAction.getValue() != null) {
                    updateMsg = String.format(KeptnCloudEventApprovalData.APPROVAL_UPDATE_MSG, "*approve*");
                    approvalFinishedResult = KeptnCloudEventDataResult.PASS;
                } else {
                    updateMsg = String.format(KeptnCloudEventApprovalData.APPROVAL_UPDATE_MSG, "*deny*");
                    approvalFinishedResult = KeptnCloudEventDataResult.FAIL;
                }

                try {
                    sendKeptnApprovalFinished(approvalTriggered, approvalFinishedResult);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    sendEventError = true;
                }

                newBlocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text(updateMsg).build()).build());
            }

            if (!sendEventError) {
                attachments = SlackCreator.createSlackAttachment(approvalFinishedResult, newBlocks, "updated message");
            }
        }

        return attachments;
    }

    /**
     * Extracts the KeptnCloudEvent out of the actionsBlock.
     * The actionsBlock must have a button with an actionId starting with the value of ApprovalMapper.APPROVAL_APPROVE_ID.
     *
     * @param actionsBlock to extract the event of
     * @return the KeptnCloudEvent if successful or else null
     */
    private KeptnCloudEvent extractKeptnCloudEvent(ActionsBlock actionsBlock) {
        KeptnCloudEvent result = null;
        ButtonElement eventButton = null;

        if (actionsBlock != null && actionsBlock.getElements() != null) {
            Iterator<BlockElement> blockIterator = actionsBlock.getElements().iterator();

            while (eventButton == null && blockIterator.hasNext()) {
                BlockElement element = blockIterator.next();

                if (element instanceof ButtonElement) {
                    ButtonElement button = (ButtonElement) element;

                    if (button.getActionId().startsWith(ApprovalMapper.APPROVAL_APPROVE_ID)) {
                        eventButton = button;
                    }
                }
            }

            if (eventButton != null) {
                try {
                    result = KeptnCloudEventParser.parseJsonToKeptnCloudEvent(eventButton.getValue());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * Creates an approval.finished event out of a given approval.triggered event.
     * The result of the .finished event is the given result.
     *
     * @param approvalTriggered out of which the approval.finished is created
     * @param approvalResult    of the approval.finished event
     */
    private void sendKeptnApprovalFinished(KeptnCloudEvent approvalTriggered, KeptnCloudEventDataResult approvalResult) {
        if (approvalTriggered != null && approvalResult != null) {
            Object approvalTriggeredDataObject = approvalTriggered.getData();

            if (approvalTriggeredDataObject instanceof KeptnCloudEventApprovalData) {
                KeptnCloudEventApprovalData approvalTriggeredData = (KeptnCloudEventApprovalData) approvalTriggeredDataObject;

                KeptnCloudEventData approvalFinishedData = new KeptnCloudEventData(approvalTriggeredData.getProject(),
                        approvalTriggeredData.getService(), approvalTriggeredData.getStage(), approvalTriggeredData.getLabels(),
                        "", KeptnCloudEventDataStatus.SUCCEEDED, approvalResult);

                KeptnCloudEvent eventFinished = new KeptnCloudEvent(Objects.toString(UUID.randomUUID()),
                        approvalTriggered.getSpecversion(), approvalTriggered.getSource(),
                        KeptnEvent.APPROVAL, KeptnEvent.FINISHED, approvalTriggered.getDatacontenttype(), approvalFinishedData,
                        approvalTriggered.getShkeptncontext(), approvalTriggered.getId(),
                        OffsetDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
                
                approvalService.sentApprovalFinished(eventFinished);
            }
        }

    }

}
