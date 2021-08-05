package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.payloadCreator.*;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.block.LayoutBlock;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@ApplicationScoped
public class SlackHandler implements KeptnCloudEventHandler {
    private static final String ENV_SLACK_TOKEN = "SLACK_TOKEN";
    private static final String ENV_SLACK_CHANNEL = "SLACK_CHANNEL";
    private static final String SLACK_NOTIFICATION_MSG = "A new Keptn Event arrived.";

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

                ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                        .channel(channel)
                        .text(SLACK_NOTIFICATION_MSG)
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
