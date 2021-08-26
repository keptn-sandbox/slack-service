package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class ServiceMapper extends KeptnCloudEventMapper {
    private static final String eventName = KeptnEvent.SERVICE.getValue();
    private static final Logger LOG = Logger.getLogger(ServiceMapper.class);

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (eventName.equals(event.getTaskName())) {
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

            if (KeptnCloudEventDataResult.PASS.equals(eventResult)) {
                specificDataSB.append(ifNotNull("The service '", serviceName, "' was successfully created in the stage '" +eventStage +"'."));
            } else if (KeptnCloudEventDataResult.FAIL.equals(eventResult)) {
                specificDataSB.append(ifNotNull("There was an error creating the service '", serviceName, "' in the stage '" +eventStage +"' !"));
            }

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            LOG.warnf(WARNING_EVENT_DATA, KeptnCloudEventData.class, eventName);
            LOG.warn("(service has no specific data class)");
        }

        return layoutBlockList;
    }
}
