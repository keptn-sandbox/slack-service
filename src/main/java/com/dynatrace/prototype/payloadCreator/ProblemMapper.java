package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProblemData;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventProblem;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class ProblemMapper extends KeptnCloudEventMapper {
    private static final String eventName = KeptnEvent.PROBLEM.getValue();
    private static final Logger LOG = Logger.getLogger(ProblemMapper.class);

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event instanceof KeptnCloudEventProblem) {
            layoutBlockList.addAll(getProblemData((KeptnCloudEventProblem) event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getProblemData(KeptnCloudEventProblem event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        KeptnCloudEventProblemData eventData = event.getData();

        if (eventData != null) {
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
            LOG.warn("EventData is null!");
        }

        return layoutBlockList;
    }
}
