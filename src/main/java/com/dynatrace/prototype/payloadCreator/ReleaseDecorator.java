package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventReleaseData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReleaseDecorator extends KeptnCloudEventDecorator {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.RELEASE.getValue())) {
            layoutBlockList.addAll(super.getSpecificData(event));
            layoutBlockList.addAll(getReleaseData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getReleaseData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventReleaseData) {
            KeptnCloudEventReleaseData eventData = (KeptnCloudEventReleaseData) eventDataObject;
            String specificDataString = "";

            specificDataString += ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n");
            specificDataString += ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n");
            specificDataString += ifNotNull("Deployment names: ", Objects.toString(eventData.getDeploymentNames()), "\n");

            if (!specificDataString.isBlank()) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataString));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventReleaseData although the event type is \"Release\"!");
        }

        return layoutBlockList;
    }
}
