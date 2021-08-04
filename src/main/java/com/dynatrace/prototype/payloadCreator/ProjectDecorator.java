package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProjectData;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProjectFinishedData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;

public class ProjectDecorator extends KeptnCloudEventDecorator {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.PROJECT.getValue())) {
            layoutBlockList.addAll(super.getSpecificData(event));
            layoutBlockList.addAll(getProjectData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getProjectData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        // KeptnCloudEventProjectData is for all "create.project" events except the ".triggered".
        // For the ".triggered" use KeptnCloudEventProjectTriggeredData
        if (eventDataObject instanceof KeptnCloudEventProjectData) {
            KeptnCloudEventProjectData eventData = (KeptnCloudEventProjectData) eventDataObject;
            KeptnCloudEventProjectFinishedData createdProject = eventData.getCreatedProject();
            String specificDataString = "";

            if (createdProject != null) {
                String projectName = createdProject.getProjectName();
                KeptnCloudEventDataResult eventResult = eventData.getResult();

                if (KeptnCloudEventDataResult.PASS.getValue().equals(eventResult.getValue())) {
                    specificDataString += ifNotNull("The project '", projectName, "' was successfully created.");
                } else if (KeptnCloudEventDataResult.FAIL.getValue().equals(eventResult.getValue())) {
                    specificDataString += ifNotNull("There was an error creating the project '", projectName, "' !");
                }
            }
            if (!specificDataString.isBlank()) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataString));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventProjectData although the event type is \"Project\"! (maybe because it is a .triggered event)");
        }

        return layoutBlockList;
    }
}
