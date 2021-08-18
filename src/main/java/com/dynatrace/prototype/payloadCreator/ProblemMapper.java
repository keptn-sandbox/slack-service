package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProblemData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProblemMapper extends KeptnCloudEventMapper {
    private static final String eventName = KeptnEvent.PROBLEM.getValue();

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (eventName.equals(event.getTaskName())) {
            layoutBlockList.addAll(getProblemData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getProblemData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventProblemData) {
            KeptnCloudEventProblemData eventData = (KeptnCloudEventProblemData) eventDataObject;
            StringBuilder message = new StringBuilder();
            String state = eventData.getState();
            String service = eventData.getService();
            String stage = eventData.getStage();
            String project = eventData.getProject();

            if (!logErrorIfNull(stage, "Stage", eventName) &&
                !logErrorIfNull(service, "Service", eventName) &&
                !logErrorIfNull(project, "Project", eventName)) {

                if (KeptnCloudEventProblemData.OPEN.equals(state)) {
                    message.append("A problem occurred in ");
                } else if (KeptnCloudEventProblemData.RESOLVED.equals(state)) {
                    message.append("Resolved a problem in ");
                }

                message.append(String.format(SERVICE_STAGE_PROJECT_TEXT, service, stage, project)).append('!');
            }

            if (message.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, message.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventProblemData although the event type is \"Problem\"!");
        }

        return layoutBlockList;
    }
}
