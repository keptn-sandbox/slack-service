package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.List;

public class ServiceDecorator extends KeptnCloudEventDecorator {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (event.getType().startsWith(KeptnEvent.SERVICE.getValue())) {
            layoutBlockList.addAll(super.getSpecificData(event));
            layoutBlockList.addAll(getServiceData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getServiceData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventData) {
            KeptnCloudEventData eventData = (KeptnCloudEventData) eventDataObject;
            StringBuilder specificDataSB = new StringBuilder();
            String serviceName = eventData.getService();
            KeptnCloudEventDataResult eventResult = eventData.getResult();
            String eventStage = eventData.getStage();

            if (KeptnCloudEventDataResult.PASS.getValue().equals(eventResult.getValue())) {
                specificDataSB.append(ifNotNull("The service '", serviceName, "' was successfully created in the stage '" +eventStage +"'."));
            } else if (KeptnCloudEventDataResult.FAIL.getValue().equals(eventResult.getValue())) {
                specificDataSB.append(ifNotNull("There was an error creating the service '", serviceName, "' in the stage '" +eventStage +"' !"));
            }

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(createSlackDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventData although the event type is \"Service\"! (service has no specific data class)");
        }

        return layoutBlockList;
    }
}
