package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProjectData;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProjectFinishedData;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventProblem;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEventProject;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class ProjectMapper extends KeptnCloudEventMapper {
    private static final String eventName = KeptnEvent.PROJECT.getValue();
    private static final Logger LOG = Logger.getLogger(ProjectMapper.class);

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event instanceof KeptnCloudEventProject) {
            layoutBlockList.addAll(getProjectData((KeptnCloudEventProject) event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getProjectData(KeptnCloudEventProject event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        KeptnCloudEventProjectData eventData = event.getData();

        // KeptnCloudEventProjectData is for all "create.project" events except the ".triggered".
        // For the ".triggered" use KeptnCloudEventProjectTriggeredData
        if (eventData != null) {
            KeptnCloudEventProjectFinishedData createdProject = eventData.getCreatedProject();
            StringBuilder specificDataSB = new StringBuilder();

            if (createdProject != null) {
                String projectName = createdProject.getProjectName();
                KeptnCloudEventDataResult eventResult = eventData.getResult();

                if (KeptnCloudEventDataResult.PASS.equals(eventResult)) {
                    specificDataSB.append(ifNotNull("The project '", projectName, "' was successfully created."));
                } else if (KeptnCloudEventDataResult.FAIL.equals(eventResult)) {
                    specificDataSB.append(ifNotNull("There was an error creating the project '", projectName, "' !"));
                }
            }

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            LOG.warn("EventData is null!");
        }

        return layoutBlockList;
    }
}
