package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.ApprovalService;
import com.dynatrace.prototype.SlackMessageSenderService;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataStatus;
import com.dynatrace.prototype.domainModel.KeptnCloudEventParser;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventApprovalData;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventApproval;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventDefault;
import com.dynatrace.prototype.payloadCreator.*;
import com.google.gson.JsonSyntaxException;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.chat.ChatUpdateRequest;
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
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class SlackHandler implements KeptnCloudEventHandler {
    private static final Logger LOG = Logger.getLogger(SlackHandler.class);

    private static final String ENV_SLACK_TOKEN = "SLACK_TOKEN";
    private static final String ENV_SLACK_CHANNEL = "SLACK_CHANNEL";
    private static final String ENV_SLACK_MSG_INTERVAL = "SLACK_MSG_INTERVAL_MILLIS";
    private static final String SLACK_NOTIFICATION_MSG = "A new Keptn Event arrived.";

    private LinkedHashSet<KeptnCloudEventMapper> mappers;
    private ConcurrentHashMap<OffsetDateTime, ChatPostMessageRequest> bufferedPostMessages;

    @Inject
    @RestClient
    ApprovalService approvalService;

    public SlackHandler() {
        this.mappers = new LinkedHashSet<>();
        this.bufferedPostMessages = new ConcurrentHashMap<>();

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

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        String token = System.getenv(ENV_SLACK_TOKEN);

        if (token == null) {
            LOG.error(ENV_SLACK_TOKEN + " is null!");
        } else {
            String intervalString = System.getenv(ENV_SLACK_MSG_INTERVAL);
            int interval = 10000;

            try {
                if (intervalString == null) {
                    LOG.info("Using default interval of 10 seconds for slack post messages.");
                } else {
                    interval = Integer.parseInt(intervalString);
                    LOG.info("Using given interval of " + intervalString + " milliseconds for slack post messages.");
                }
            } catch (NumberFormatException e) {
                LOG.error("Default interval of 10 seconds for slack post messages provided.", e);
            }

            executor.scheduleAtFixedRate(new SlackMessageSenderService(bufferedPostMessages, token), 0, interval, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean receiveEvent(KeptnCloudEvent event) {
        boolean successful = false;
        String channel = System.getenv(ENV_SLACK_CHANNEL);

        if (channel == null) {
            LOG.error(ENV_SLACK_CHANNEL + " is null!");
        } else if (event == null) {
            LOG.error("KeptnCloudEvent is null!");
        } else {
            List<LayoutBlock> layoutBlocks = new ArrayList<>();

            for (KeptnCloudEventMapper mapper : mappers) {
                layoutBlocks.addAll(mapper.getSpecificData(event));
            }

            try {
                ChatPostMessageRequest request = SlackCreator.createPostRequest(event, channel, layoutBlocks, SLACK_NOTIFICATION_MSG);
                OffsetDateTime requestKey = OffsetDateTime.parse(event.getTime());

                bufferedPostMessages.put(requestKey, request);

                if(request.equals(bufferedPostMessages.get(requestKey))) {
                    LOG.info("Post message added!");
                    successful = true;
                } else {
                    LOG.info("Failed to add post message!");
                }
            } catch (Exception e) {
                LOG.error("An exception occurred while receiving an event!", e);
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
                    LOG.error("PAYLOAD_ERROR: " + response.getError());
                }
            } catch (Exception e) {
                LOG.error("An exception occurred while updating a Slack message!", e);
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
            KeptnCloudEventApproval approvalTriggered = null;
            String barColor = null;

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

                KeptnCloudEvent keptnCloudEvent = extractKeptnCloudEvent(firstActions);
                if (keptnCloudEvent instanceof KeptnCloudEventApproval) {
                    approvalTriggered = (KeptnCloudEventApproval) keptnCloudEvent;
                }
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

                barColor = SlackCreator.getEventResultColor(approvalFinishedResult.getValue());
                if (barColor == null && firstAttachment != null) {
                    barColor = firstAttachment.getColor();
                }

                try {
                    sendKeptnApprovalFinished(approvalTriggered, approvalFinishedResult);
                } catch (Exception e) {
                    LOG.error("An exception occurred while sending the approval.finished event!", e);
                    sendEventError = true;
                }

                newBlocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text(updateMsg).build()).build());
            }

            if (!sendEventError) {
                attachments = SlackCreator.createSlackAttachment(barColor, newBlocks, "updated message");
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
                } catch (JsonSyntaxException e) {
                    LOG.error("An exception occurred while parsing JSON to an event!");
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
    private void sendKeptnApprovalFinished(KeptnCloudEventApproval approvalTriggered, KeptnCloudEventDataResult approvalResult) {
        if (approvalTriggered != null && approvalResult != null) {
            KeptnCloudEventApprovalData approvalTriggeredData = approvalTriggered.getData();

            if (approvalTriggeredData != null) {
                KeptnCloudEventData approvalFinishedData = new KeptnCloudEventData(approvalTriggeredData.getProject(),
                        approvalTriggeredData.getService(), approvalTriggeredData.getStage(), approvalTriggeredData.getLabels(),
                        "", KeptnCloudEventDataStatus.SUCCEEDED, approvalResult);

                KeptnCloudEventDefault eventFinished = new KeptnCloudEventDefault(Objects.toString(UUID.randomUUID()),
                        approvalTriggered.getSpecversion(), approvalTriggered.getSource(),
                        KeptnEvent.APPROVAL, KeptnEvent.FINISHED, approvalTriggered.getDatacontenttype(),
                        approvalTriggered.getShkeptncontext(), approvalTriggered.getId(),
                        OffsetDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME), approvalFinishedData);
                
                approvalService.sentApprovalFinished(eventFinished);
            }
        }

    }

}
