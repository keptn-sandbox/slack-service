package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventActionData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.apache.maven.shared.utils.StringUtils;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class GetActionMapper extends KeptnCloudEventMapper {
    private static String eventName = KeptnEvent.ACTION.getValue();
    private static final Logger LOG = Logger.getLogger(GetActionMapper.class);

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (KeptnEvent.ACTION.getValue().equals(event.getTaskName())) {
            layoutBlockList.addAll(getActionData(event));
        } else if (KeptnEvent.GET_ACTION.getValue().equals(event.getTaskName())) {
            eventName = KeptnEvent.GET_ACTION.getValue();
            layoutBlockList.addAll(getActionData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getActionData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventActionData) {
            KeptnCloudEventActionData eventData = (KeptnCloudEventActionData) eventDataObject;
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
            LOG.warnf(WARNING_EVENT_DATA, KeptnCloudEventActionData.class, eventName);
        }

        return layoutBlockList;
    }
}
