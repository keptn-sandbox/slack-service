package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventGetSLIData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;

public class GetSLIMapper extends KeptnCloudEventMapper {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (KeptnEvent.GET_SLI.getValue().equals(event.getTaskName())) {
            layoutBlockList.addAll(getSLIData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getSLIData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventGetSLIData) {
            KeptnCloudEventGetSLIData eventData = (KeptnCloudEventGetSLIData) eventDataObject;
            StringBuilder specificDataSB = new StringBuilder();

            specificDataSB.append(ifNotNull("Sli Provider: ", eventData.getSliProvider(), "\n"));

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventGetSLIData although the event type is \"Get-Sli\"!");
        }

        return layoutBlockList;
    }
}
