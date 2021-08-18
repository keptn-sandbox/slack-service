package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GeneralEventMapper extends KeptnCloudEventMapper {
    private static final String ENV_KEPTN_BRIDGE_DOMAIN = "KEPTN_BRIDGE_DOMAIN";
    private static final String APP_LAYER_PROTOCOL = "http";
    private static final String KEPTN_BRIDGE_NAME = "Keptn bridge";
    private static final String KEPTN_BRIDGE_DASHBOARD = "/bridge/dashboard";
    private static final String KEPTN_BRIDGE_PROJECT = "/bridge/project/";

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        return getGeneralData(event);
    }

    private List<LayoutBlock> getGeneralData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        //TODO: implement name for sequence event type
        String eventName = event.getTaskName();
        eventName = StringUtils.capitalise(eventName);

        if (event.getPlainEventType() != null) {
            eventName += " - " +event.getPlainEventType();
        }

        if (eventName != null) {
            layoutBlockList.add(SlackCreator.createLayoutBlock(HeaderBlock.TYPE, eventName));
        }

        String keptnBridgeDomain = System.getenv(ENV_KEPTN_BRIDGE_DOMAIN);
        if (keptnBridgeDomain == null) {
            System.err.println(ENV_KEPTN_BRIDGE_DOMAIN +" is null!");
        } else {
            Object eventDataObject = event.getData();

            if (eventDataObject instanceof KeptnCloudEventData) {
                KeptnCloudEventData eventData = (KeptnCloudEventData) eventDataObject;
                StringBuilder eventURLSB = new StringBuilder();
                eventURLSB.append(APP_LAYER_PROTOCOL).append("://").append(keptnBridgeDomain).append(KEPTN_BRIDGE_DASHBOARD);

                if (eventData.getProject() != null) {
                    eventURLSB.setLength(0); //clears its content
                    eventURLSB.append(APP_LAYER_PROTOCOL).append("://").append(keptnBridgeDomain)
                            .append(KEPTN_BRIDGE_PROJECT).append(eventData.getProject()).append("/sequence/")
                            .append(event.getShkeptncontext()).append("/event/").append(event.getId());
                }

                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, SlackCreator.formatLink(eventURLSB.toString(),
                        KEPTN_BRIDGE_NAME)));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        }

        return layoutBlockList;
    }
}
