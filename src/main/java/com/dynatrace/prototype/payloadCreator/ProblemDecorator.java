package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProblemData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;

public class ProblemDecorator extends KeptnCloudEventDecorator {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().contains("problem") || event.getType().startsWith(KeptnEvent.PROBLEM.getValue())) {//TODO: check if Problem events start with sh.keptn.events or without the "s"
            layoutBlockList.addAll(super.getSpecificData(event));
            layoutBlockList.addAll(getProblemData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getProblemData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventProblemData) {
            KeptnCloudEventProblemData eventData = (KeptnCloudEventProblemData) eventDataObject;
            String specificDataString = "";

            specificDataString += ifNotNull("State: ", eventData.getState(), "\n");
            specificDataString += ifNotNull("Problem ID: ", eventData.getProblemID(), "\n");
            specificDataString += ifNotNull("Problem Title: ", eventData.getProblemTitle(), "\n");
            specificDataString += ifNotNull(null, formatLink(eventData.getProblemURL(), "Problem URL"), "\n");

            if (!specificDataString.isBlank()) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataString));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventProblemData although the event type is \"Problem\"!");
        }

        return layoutBlockList;
    }
}
