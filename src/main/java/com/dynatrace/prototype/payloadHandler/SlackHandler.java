package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.payloadCreator.SlackPayloadCreator;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.SlackApiRequest;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.block.LayoutBlock;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class SlackHandler implements KeptnCloudEventHandler {
    private static final String ENV_SLACK_TOKEN = "SLACK_TOKEN";
    private static final String ENV_SLACK_CHANNEL = "SLACK_CHANNEL";

    @Override
    public boolean handleEvent(KeptnCloudEvent event) {
        boolean successful = false;
        SlackPayloadCreator slackPayloadCreator = new SlackPayloadCreator();
        Slack slack = Slack.getInstance();
        String token = System.getenv(ENV_SLACK_TOKEN);
        String channel = System.getenv(ENV_SLACK_CHANNEL);

        if (token == null) {
            System.err.println(ENV_SLACK_TOKEN +" is null!");
        } else if (channel == null) {
            System.err.println(ENV_SLACK_CHANNEL +" is null!");
        } else {
            try {
                List<LayoutBlock> layoutBlockList = slackPayloadCreator.createPayload(event);
                ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                        .channel(channel)
                        .text("A new Keptn Event arrived.")
                        .blocks(layoutBlockList)
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
}
