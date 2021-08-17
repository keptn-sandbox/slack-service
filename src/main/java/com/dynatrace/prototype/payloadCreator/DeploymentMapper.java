package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventDeploymentData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeploymentMapper extends KeptnCloudEventMapper {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (KeptnEvent.DEPLOYMENT.getValue().equals(event.getTaskName())) {
            layoutBlockList.addAll(getDeploymentData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getDeploymentData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventDeploymentData) {
            KeptnCloudEventDeploymentData eventData = (KeptnCloudEventDeploymentData) eventDataObject;
            StringBuilder specificDataSB = new StringBuilder();

            specificDataSB.append(ifNotNull(null, SlackCreator.formatLink(eventData.getFirstDeploymentURIPublic(),
                    "Public URI"), "\n"));
            specificDataSB.append(ifNotNull(null, SlackCreator.formatLink(eventData.getFirstDeploymentURILocal(),
                    "Local URI"), "\n"));
            specificDataSB.append(ifNotNull("Deployment names: ", Objects.toString(eventData.getDeploymentNames()),
                    "\n"));

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventDeploymentData although the event type is \"Deployment\"!");
        }

        return layoutBlockList;
    }
}
