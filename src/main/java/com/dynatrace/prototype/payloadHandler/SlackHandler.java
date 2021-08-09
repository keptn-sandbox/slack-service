package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.dynatrace.prototype.payloadCreator.*;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.chat.ChatUpdateRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.chat.ChatUpdateResponse;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;

@ApplicationScoped
public class SlackHandler implements KeptnCloudEventHandler {
    private static final String ENV_SLACK_TOKEN = "SLACK_TOKEN";
    private static final String ENV_SLACK_CHANNEL = "SLACK_CHANNEL";
    private static final String SLACK_NOTIFICATION_MSG = "A new Keptn Event arrived.";
    private static final String COLOR_PASS = "#00FF00";
    private static final String COLOR_WARNING = "#FFFF00";
    private static final String COLOR_FAIL = "#FF0000";

    private LinkedHashSet<KeptnCloudEventMapper> mappers;

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
    public boolean handleEvent(KeptnCloudEvent event) {
        boolean successful = false;
        Slack slack = Slack.getInstance();
        String token = System.getenv(ENV_SLACK_TOKEN);
        String channel = System.getenv(ENV_SLACK_CHANNEL);

        if (token == null) {
            System.err.println(ENV_SLACK_TOKEN +" is null!");
        } else if (channel == null) {
            System.err.println(ENV_SLACK_CHANNEL +" is null!");
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
                    System.err.println("PAYLOAD_ERROR: " +response.getError());
                }
            } catch (IOException | SlackApiException e) {
                System.err.println("EXCEPTION: " +e.getMessage());
                e.printStackTrace();
            }
        }

        return successful;
    }

    public boolean sendEvent(BlockActionPayload payload) {
        boolean successful = false;

        Slack slack = Slack.getInstance();
        String token = System.getenv(ENV_SLACK_TOKEN);

        List<Attachment> attachments = payload.getMessage().getAttachments();

        if (attachments.size() > 0) {
            List<BlockActionPayload.Action> actions = payload.getActions();
            Attachment attachment = attachments.get(0);
            List<LayoutBlock> newBlocks;
            List<LayoutBlock> oldBlocks = attachment.getBlocks();
            ListIterator<LayoutBlock> blockIterator = oldBlocks.listIterator();
            int firstDividerIndex = oldBlocks.size();

            while (firstDividerIndex == oldBlocks.size() && blockIterator.hasNext()) {
                LayoutBlock current = blockIterator.next();

                if (current instanceof DividerBlock) {
                    firstDividerIndex = blockIterator.nextIndex();
                }
            }

            newBlocks = oldBlocks.subList(0, firstDividerIndex);
            if (!newBlocks.isEmpty()) {
                if (actions.size() > 0) {
                    BlockActionPayload.Action action = actions.get(0);
                    //TODO: handle value of pressed button with action.getValue() to approve / deny the approval
                    //TODO: need to use methods from mapper to create blocks
                    newBlocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text("*" +action.getText().getText() +"*").build()).build());
                    attachment.setBlocks(newBlocks);
                }
            }

        }

        ChatUpdateRequest updateRequest = ChatUpdateRequest.builder()
                .channel(payload.getChannel().getId())
                .ts(payload.getMessage().getTs())
                .attachments(attachments)
                .build();

        try {
            ChatUpdateResponse response = slack.methods(token).chatUpdate(updateRequest);

            if (response.isOk()) {
                successful = true;
            } else {
                System.err.println("PAYLOAD_ERROR: " +response.getError());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SlackApiException e) {
            e.printStackTrace();
        }


        return successful;
    }

    /**
     * Creates a list of slack attachments with one attachment with the given list as blocks.
     * Evaluate the result of the keptn event and changes the color of the attachment accordingly.
     * @param event Keptn Cloud Event to evaluate the result
     * @param layoutBlocks that are added to the attachment
     * @param fallback of the attachment (e.g. message of notification)
     * @return List<Attachment> with one attachment
     */
    private List<Attachment> createSlackAttachment(KeptnCloudEvent event, List<LayoutBlock> layoutBlocks, String fallback) {
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        Object eventDataObject = event.getData();

        attachment.setBlocks(layoutBlocks);
        attachment.setFallback(fallback);
        if (eventDataObject instanceof KeptnCloudEventData) {
            KeptnCloudEventDataResult result = ((KeptnCloudEventData) eventDataObject).getResult();

            if (KeptnCloudEventDataResult.PASS.getValue().equals(result.getValue())) {
                attachment.setColor(COLOR_PASS);
            } else if (KeptnCloudEventDataResult.WARNING.getValue().equals(result.getValue())) {
                attachment.setColor(COLOR_WARNING);
            } else if (KeptnCloudEventDataResult.FAIL.getValue().equals(result.getValue())) {
                attachment.setColor(COLOR_FAIL);
            }
        }
        attachments.add(attachment);

        return attachments;
    }
}
