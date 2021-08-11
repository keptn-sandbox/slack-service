package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.ApprovalService;
import com.dynatrace.prototype.domainModel.*;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventApprovalData;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProblemData;
import com.dynatrace.prototype.payloadCreator.*;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.time.LocalDateTime;
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

                List<Attachment> attachments = createSlackAttachment(event, layoutBlockList, SLACK_NOTIFICATION_MSG);
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
            List<Attachment> attachments = actionPayload.getMessage().getAttachments();

            if (attachments.size() > 0) {
                List<BlockActionPayload.Action> actions = actionPayload.getActions();
                Attachment attachment = attachments.get(0);
                List<LayoutBlock> newBlocks;
                List<LayoutBlock> oldBlocks = attachment.getBlocks();
                ListIterator<LayoutBlock> blockIterator = oldBlocks.listIterator();
                int firstDividerIndex = oldBlocks.size();
                ActionsBlock firstActionBlock = null;
                ButtonElement buttonWithEvent = null;

                while ((firstDividerIndex == oldBlocks.size() || firstActionBlock == null) && blockIterator.hasNext()) {
                    LayoutBlock current = blockIterator.next();

                    if (current instanceof DividerBlock) {
                        firstDividerIndex = blockIterator.nextIndex();
                    } else if (current instanceof ActionsBlock) {
                        firstActionBlock = (ActionsBlock) current;
                    }
                }

                if (firstActionBlock != null && firstActionBlock.getElements() != null) {
                    Iterator<BlockElement> buttonIterator = firstActionBlock.getElements().iterator();

                    while (buttonWithEvent == null && buttonIterator.hasNext()) {
                        BlockElement element = buttonIterator.next();

                        if (element instanceof ButtonElement) {
                            ButtonElement button = (ButtonElement) element;

                            if (button.getActionId().startsWith(ApprovalMapper.APPROVAL_APPROVE_ID)) {
                                buttonWithEvent = button;
                            }
                        }
                    }
                }

                newBlocks = oldBlocks.subList(0, firstDividerIndex);
                if (!actions.isEmpty() && buttonWithEvent != null) {
                    BlockActionPayload.Action action = actions.get(0);

                    try {
                        KeptnCloudEvent eventTriggered = KeptnCloudEventParser.parseJsonToKeptnCloudEvent(buttonWithEvent.getValue());
                        Object eventTrigDataObject = eventTriggered.getData();

                        if (eventTrigDataObject instanceof KeptnCloudEventApprovalData) {
                            KeptnCloudEventApprovalData eventTrigData = (KeptnCloudEventApprovalData) eventTrigDataObject;
                            KeptnCloudEventDataResult result = KeptnCloudEventDataResult.FAIL;

                            //TODO: maybe create own class for creating slack blocks
                            if (ApprovalMapper.APPROVAL_APPROVE_VALUE.equals(action.getText().getText())) {
                                result = KeptnCloudEventDataResult.PASS;
                            }

                            KeptnCloudEventData eventFiniData = new KeptnCloudEventData(eventTrigData.getProject(),
                                    eventTrigData.getService(), eventTrigData.getStage(), eventTrigData.getLabels(),
                                    "", KeptnCloudEventDataStatus.SUCCEEDED, result);
                            KeptnCloudEvent eventFinished = new KeptnCloudEvent(eventTriggered.getSpecversion(),
                                    eventTriggered.getSource(), KeptnEvent.APPROVAL_FINISHED.getValue(),
                                    eventTriggered.getDatacontenttype(), eventFiniData, eventTriggered.getShkeptncontext(),
                                    eventTriggered.getId(), LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

                            approvalService.sentApprovalFinished(eventFinished);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    newBlocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text("*" + action.getText().getText() + "*").build()).build());
                    attachment.setBlocks(newBlocks);
                }
            }

            ChatUpdateRequest updateRequest = ChatUpdateRequest.builder()
                    .channel(actionPayload.getChannel().getId())
                    .ts(actionPayload.getMessage().getTs())
                    .attachments(attachments)
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
     * Creates a list of slack attachments with one attachment with the given list as blocks.
     * Evaluate the result of the keptn event and changes the color of the attachment accordingly.
     *
     * @param event        Keptn Cloud Event to evaluate the result
     * @param layoutBlocks that are added to the attachment
     * @param fallback     of the attachment (e.g. message of notification)
     * @return List<Attachment> with one attachment
     */
    private List<Attachment> createSlackAttachment(KeptnCloudEvent event, List<LayoutBlock> layoutBlocks, String fallback) {
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        Object eventDataObject = event.getData();

        attachment.setBlocks(layoutBlocks);
        attachment.setFallback(fallback);
        if (eventDataObject instanceof KeptnCloudEventData) {
            KeptnCloudEventData eventData = (KeptnCloudEventData) eventDataObject;

            if (eventData.getResult() != null) {
                attachment.setColor(getEventResultColor(Objects.toString(eventData.getResult())));
            } else if (eventData instanceof KeptnCloudEventProblemData) {
                KeptnCloudEventProblemData eventProblemData = (KeptnCloudEventProblemData) eventData;
                attachment.setColor(getEventResultColor(eventProblemData.getState()));
            }
        }
        attachments.add(attachment);

        return attachments;
    }

    private String getEventResultColor(String result) {
        String eventResultColor = null;

        if (result != null) {
            if (KeptnCloudEventDataResult.PASS.getValue().equals(result) || KeptnCloudEventProblemData.RESOLVED.equals(result)) {
                eventResultColor = COLOR_PASS;
            } else if (KeptnCloudEventDataResult.WARNING.getValue().equals(result)) {
                eventResultColor = COLOR_WARNING;
            } else if (KeptnCloudEventDataResult.FAIL.getValue().equals(result) || KeptnCloudEventProblemData.OPEN.equals(result)) {
                eventResultColor = COLOR_FAIL;
            }
        }

        return eventResultColor;
    }
}
