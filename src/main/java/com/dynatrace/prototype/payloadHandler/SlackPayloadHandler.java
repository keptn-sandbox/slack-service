package com.dynatrace.prototype.payloadHandler;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

@ApplicationScoped
public class SlackPayloadHandler implements PayloadHandler {
    private static final String ENV_SLACK_TOKEN = "SLACK_TOKEN";
    private static final String ENV_SLACK_CHANNEL = "SLACK_CHANNEL";

    @Override
    public boolean sendPayload(String payload) {
        boolean messageSent = false;
        Slack slack = Slack.getInstance();
        String token = System.getenv(ENV_SLACK_TOKEN);
        String channel = System.getenv(ENV_SLACK_CHANNEL);

        if (!payload.isBlank()) {
            try {
                ChatPostMessageResponse response = slack.methods(token).chatPostMessage(ChatPostMessageRequest.builder().channel(channel).blocksAsString(payload).build());
                if (response.isOk()) {
                    messageSent = true;
                } else {
                    System.err.println("PAYLOAD_ERROR: " +response.getError());
                    System.out.println("SLACK_TOKEN is blank: " +System.getenv(ENV_SLACK_TOKEN).isBlank());
                    System.out.println("SLACK_CHANNEL: " +System.getenv(ENV_SLACK_CHANNEL));
                }
            } catch (IOException | SlackApiException e) {
                System.err.println("EXCEPTION: " +e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("WARN: Tried to send blank Payload to Slack.");
        }

        return messageSent;
    }
}
