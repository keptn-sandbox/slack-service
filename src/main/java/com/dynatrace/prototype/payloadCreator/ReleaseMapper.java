package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventReleaseData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ReleaseMapper extends KeptnCloudEventMapper {
    private static final KeptnEvent eventName = KeptnEvent.RELEASE;

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (eventName.getValue().equals(event.getTaskName())) {
            layoutBlockList.addAll(getReleaseData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getReleaseData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventReleaseData) {
            KeptnCloudEventReleaseData eventData = (KeptnCloudEventReleaseData) eventDataObject;
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
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventReleaseData although the event type is \"Release\"!");
        }

        return layoutBlockList;
    }
}
