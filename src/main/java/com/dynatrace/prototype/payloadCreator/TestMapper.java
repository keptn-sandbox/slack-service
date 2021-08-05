package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventTestData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;

public class TestMapper extends KeptnCloudEventMapper {
    
    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.TEST.getValue())) {
            layoutBlockList.addAll(getTestData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getTestData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventTestData) {
            KeptnCloudEventTestData eventData = (KeptnCloudEventTestData) eventDataObject;
            StringBuilder specificDataSB = new StringBuilder();

            specificDataSB.append(ifNotNull("Start: ", eventData.getTestStart(), "\n"));
            specificDataSB.append(ifNotNull("End: ", eventData.getTestEnd(), "\n"));
            specificDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n"));
            specificDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n"));
            specificDataSB.append(ifNotNull("Git commit: ", eventData.getTestGitCommit(), "\n"));

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventTestData although the event type is \"Test\"!");
        }

        return layoutBlockList;
    }
}
