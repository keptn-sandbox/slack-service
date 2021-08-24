package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventActionData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetActionMapper extends KeptnCloudEventMapper {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (KeptnEvent.GET_ACTION.getValue().equals(event.getTaskName()) || KeptnEvent.ACTION.getValue().equals(event.getTaskName())) {
            layoutBlockList.addAll(getActionData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getActionData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventActionData) {
            KeptnCloudEventActionData eventData = (KeptnCloudEventActionData) eventDataObject;
            StringBuilder specificDataSB = new StringBuilder();

            specificDataSB.append(ifNotNull("Problem title: ", eventData.getProblemTitle(), "\n"));
            specificDataSB.append(ifNotNull("Problem root cause: ", eventData.getProblemRootCause(), "\n"));
            specificDataSB.append(ifNotNull("Action name: ", eventData.getActionName(), "\n"));
            specificDataSB.append(ifNotNull("Action: ", eventData.getRealAction(), "\n"));
            specificDataSB.append(ifNotNull("Action description: ", eventData.getActionDescription(), "\n"));
            specificDataSB.append(ifNotNull("Additional action values: ", Objects.toString(eventData.getAdditionalActionValues()), "\n"));

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventActionData although the event type is \"Action / Get-Action\"!");
        }

        return layoutBlockList;
    }
}
