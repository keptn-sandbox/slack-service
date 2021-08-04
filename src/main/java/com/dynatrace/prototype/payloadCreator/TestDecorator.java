package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventTestData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;

public class TestDecorator extends KeptnCloudEventDecorator {
    
    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.TEST.getValue())) {
            layoutBlockList.addAll(super.getSpecificData(event));
            layoutBlockList.addAll(getTestData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getTestData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventTestData) {
            KeptnCloudEventTestData eventData = (KeptnCloudEventTestData) eventDataObject;
            String specificDataString = "";

            specificDataString += ifNotNull("Start: ", eventData.getTestStart(), "\n");
            specificDataString += ifNotNull("End: ", eventData.getTestEnd(), "\n");
            specificDataString += ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n");
            specificDataString += ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n");
            specificDataString += ifNotNull("Git commit: ", eventData.getTestGitCommit(), "\n");

            if (!specificDataString.isBlank()) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataString));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventTestData although the event type is \"Test\"!");
        }

        return layoutBlockList;
    }
}
