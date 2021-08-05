package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventReleaseData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReleaseMapper extends KeptnCloudEventMapper {

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
            StringBuilder specificDataSB = new StringBuilder();

            specificDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURIPublic(), "Public URI"), "\n"));
            specificDataSB.append(ifNotNull(null, formatLink(eventData.getFirstDeploymentURILocal(), "Local URI"), "\n"));
            specificDataSB.append(ifNotNull("Deployment names: ", Objects.toString(eventData.getDeploymentNames()), "\n"));

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventReleaseData although the event type is \"Release\"!");
        }

        return layoutBlockList;
    }
}
