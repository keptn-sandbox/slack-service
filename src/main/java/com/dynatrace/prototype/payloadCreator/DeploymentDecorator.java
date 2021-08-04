package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventDeploymentData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeploymentDecorator extends KeptnCloudEventDecorator {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.DEPLOYMENT.getValue())) {
            layoutBlockList.addAll(super.getSpecificData(event));
            layoutBlockList.addAll(getDeploymentData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getDeploymentData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventDeploymentData) {
            KeptnCloudEventDeploymentData eventData = (KeptnCloudEventDeploymentData) eventDataObject;
            String specificDataString = "";

            specificDataString += ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n");
            specificDataString += ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n");
            specificDataString += ifNotNull("Deployment names: ", Objects.toString(eventData.getDeploymentNames()), "\n");

            if (!specificDataString.isBlank()) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataString));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventDeploymentData although the event type is \"Deployment\"!");
        }

        return layoutBlockList;
    }
}
