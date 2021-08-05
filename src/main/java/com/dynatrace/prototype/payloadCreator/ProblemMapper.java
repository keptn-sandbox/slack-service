package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProblemData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;

public class ProblemMapper extends KeptnCloudEventMapper {

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
            StringBuilder specificDataSB = new StringBuilder();

            specificDataSB.append(ifNotNull("State: ", eventData.getState(), "\n"));
            specificDataSB.append(ifNotNull("Problem ID: ", eventData.getProblemID(), "\n"));
            specificDataSB.append(ifNotNull("Problem Title: ", eventData.getProblemTitle(), "\n"));
            specificDataSB.append(ifNotNull(null, formatLink(eventData.getProblemURL(), "Problem URL"), "\n"));

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventProblemData although the event type is \"Problem\"!");
        }

        return layoutBlockList;
    }
}
