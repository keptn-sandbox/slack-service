package com.dynatrace.prototype;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class SlackMessageSenderService implements Runnable {
    private final ConcurrentHashMap<OffsetDateTime, ChatPostMessageRequest> bufferedPostMessages;
    private final String slackToken;

    public SlackMessageSenderService(ConcurrentHashMap<OffsetDateTime, ChatPostMessageRequest> bufferedPostMessages, String slackToken) {
        this.bufferedPostMessages = bufferedPostMessages;
        this.slackToken = slackToken;
    }

    @Override
    public void run() {
        Slack slack = Slack.getInstance();
        TreeSet<OffsetDateTime> msgKeySet = new TreeSet<>(bufferedPostMessages.keySet()); //due to the TreeSet the times are sorted by the natural order which is ascending so the earliest time is first

        for (OffsetDateTime currentKey : msgKeySet) {
            ChatPostMessageRequest request = bufferedPostMessages.remove(currentKey);

            try {
                ChatPostMessageResponse response = slack.methods(slackToken).chatPostMessage(request);

                if (response.isOk()) {
                    System.out.println("Send slack message successfully!");
                } else {
                    System.err.println("PAYLOAD_ERROR: " + response.getError());
                }
            } catch (IOException | SlackApiException e) {
                e.printStackTrace();
            }
        }
    }

}
