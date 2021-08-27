package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventDeploymentData;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventDeployment;
import com.google.gson.Gson;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.apache.maven.shared.utils.StringUtils;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class DeploymentMapper extends KeptnCloudEventMapper {
    private static final String eventName = KeptnEvent.DEPLOYMENT.getValue();
    private static final Logger LOG = Logger.getLogger(DeploymentMapper.class);

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event instanceof KeptnCloudEventDeployment) {
            layoutBlockList.addAll(getDeploymentData((KeptnCloudEventDeployment) event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getDeploymentData(KeptnCloudEventDeployment event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        KeptnCloudEventDeploymentData eventData = event.getData();

        if (eventData != null) {
            StringBuilder message = new StringBuilder();
            KeptnCloudEventDataResult result = eventData.getResult();
            KeptnEvent eventType = KeptnEvent.valueOf(StringUtils.upperCase(event.getPlainEventType()));
            String service = eventData.getService();
            String stage = eventData.getStage();
            String project = eventData.getProject();

            logErrorIfNull(stage, "Stage", eventName);
            logErrorIfNull(service, "Service", eventName);
            logErrorIfNull(project, "Project", eventName);

            message.append(createMessage(result, eventType, eventName, service, stage, project));

            if (message.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, message.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            LOG.warn("EventData is null!");
        }

        return layoutBlockList;
    }
}
