package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventActionData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetActionDecorator extends KeptnCloudEventDecorator {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.GET_ACTION.getValue()) || event.getType().startsWith(KeptnEvent.ACTION.getValue())) {
            layoutBlockList.addAll(super.getSpecificData(event));
            layoutBlockList.addAll(getActionData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getActionData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventActionData) {
            KeptnCloudEventActionData eventData = (KeptnCloudEventActionData) eventDataObject;
            String specificDataString = "";

            specificDataString += ifNotNull("Problem title: ", eventData.getProblemTitle(), "\n");
            specificDataString += ifNotNull("Problem root cause: ", eventData.getProblemRootCause(), "\n");
            specificDataString += ifNotNull("Action name: ", eventData.getActionName(), "\n");
            specificDataString += ifNotNull("Action: ", eventData.getRealAction(), "\n");
            specificDataString += ifNotNull("Action description: ", eventData.getActionDescription(), "\n");
            specificDataString += ifNotNull("Additional action values: ", Objects.toString(eventData.getAdditionalActionValues()), "\n");

            if (!specificDataString.isBlank()) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataString));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventActionData although the event type is \"Action / Get-Action\"!");
        }

        return layoutBlockList;
    }
}
