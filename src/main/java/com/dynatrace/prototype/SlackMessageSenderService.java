package com.dynatrace.prototype;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class SlackMessageSenderService implements Runnable {
    private ConcurrentHashMap<OffsetDateTime, ChatPostMessageRequest> bufferedPostMsgs;
    private final String slackToken;

    public SlackMessageSenderService(ConcurrentHashMap<OffsetDateTime, ChatPostMessageRequest> bufferedPostMsgs, String slackToken) {
        this.bufferedPostMsgs = bufferedPostMsgs;
        this.slackToken = slackToken;
    }

    @Override
    public void run() {
        Slack slack = Slack.getInstance();
        Iterator<OffsetDateTime> iterator = new TreeSet<>(bufferedPostMsgs.keySet()).iterator();


        while (iterator.hasNext()) {
            try {
                OffsetDateTime currentKey = iterator.next();
                ChatPostMessageRequest request = bufferedPostMsgs.get(currentKey);
                ChatPostMessageResponse response = slack.methods(slackToken).chatPostMessage(request);

                if (response.isOk()) {
                    System.out.println("Send slack message successfully!");
                } else {
                    System.err.println("PAYLOAD_ERROR: " + response.getError());
                }

                bufferedPostMsgs.remove(currentKey);
            } catch (IOException | SlackApiException e) {
                e.printStackTrace();
            }
        }
    }
}
