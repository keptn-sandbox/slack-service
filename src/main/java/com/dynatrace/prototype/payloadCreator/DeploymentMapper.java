package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventDeploymentData;
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

        if (eventName.equals(event.getTaskName())) {
            layoutBlockList.addAll(getDeploymentData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getDeploymentData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventDeploymentData) {
            KeptnCloudEventDeploymentData eventData = (KeptnCloudEventDeploymentData) eventDataObject;
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
            LOG.warnf(WARNING_EVENT_DATA, KeptnCloudEventDeploymentData.class, eventName);
        }

        return layoutBlockList;
    }
}
