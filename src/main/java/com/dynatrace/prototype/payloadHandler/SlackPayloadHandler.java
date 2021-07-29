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
        boolean successful = false;
        Slack slack = Slack.getInstance();
        String token = System.getenv(ENV_SLACK_TOKEN);
        String channel = System.getenv(ENV_SLACK_CHANNEL);

        if (token == null) {
            System.err.println(ENV_SLACK_TOKEN +" is null!");
        } else if (channel == null) {
            System.err.println(ENV_SLACK_CHANNEL +" is null!");
        } else if (payload.isBlank()) {
            successful = true;
        } else {
            try {
                ChatPostMessageResponse response = slack.methods(token).chatPostMessage(ChatPostMessageRequest.builder().channel(channel).blocksAsString(payload).build());

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
