package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventActionData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetActionMapper extends KeptnCloudEventMapper {
    private static KeptnEvent eventName = KeptnEvent.ACTION;

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (KeptnEvent.ACTION.getValue().equals(event.getTaskName())) {
            layoutBlockList.addAll(getActionData(event));
        } else if (KeptnEvent.GET_ACTION.getValue().equals(event.getTaskName())) {
            eventName = KeptnEvent.GET_ACTION;
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

            if (stage == null) {
                System.err.printf(ERROR_NULL_VALUE, "Stage", eventName);
            } else if (service == null) {
                System.err.printf(ERROR_NULL_VALUE, "Service", eventName);
            } else if (project == null) {
                System.err.printf(ERROR_NULL_VALUE, "Project", eventName);
            } else {
                message.append(createMessage(result, eventType, eventName, service, stage, project));
            }

            if (message.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, message.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventActionData although the event type is \"Action / Get-Action\"!");
        }

        return layoutBlockList;
    }
}
