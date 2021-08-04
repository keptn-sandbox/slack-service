package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventApprovalData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;

public class ApprovalDecorator extends KeptnCloudEventDecorator {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.APPROVAL.getValue())) {
            layoutBlockList.addAll(super.getSpecificData(event));
            layoutBlockList.addAll(getApprovalData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getApprovalData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventApprovalData) {
            KeptnCloudEventApprovalData eventData = (KeptnCloudEventApprovalData) eventDataObject;
            String specificDataString = "";

            specificDataString += ifNotNull("Pass: ", eventData.getApprovalPass(), "\n");
            specificDataString += ifNotNull("Warning: ", eventData.getApprovalWarning(), "\n");

            if (!specificDataString.isBlank()) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataString));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventApprovalData although the event type is \"Approval\"!");
        }

        return layoutBlockList;
    }
}
