package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.apache.maven.shared.utils.StringUtils;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class GeneralEventMapper extends KeptnCloudEventMapper {
    private static final Logger LOG = Logger.getLogger(GeneralEventMapper.class);
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
        StringBuilder eventName = new StringBuilder();
        boolean isSequenceEvent = false;

        if (event.getTaskName() != null) {
            eventName.append(StringUtils.capitalise(event.getTaskName()));
        } else if (event.getStageName() != null && event.getSequenceName() != null) {
            isSequenceEvent = true;
            eventName.append(event.getStageName()).append('.').append(StringUtils.capitalise(event.getSequenceName()));
        }

        if (eventName.length() > 0) {
            if (event.getPlainEventType() != null) {
                eventName.append(" - ").append(event.getPlainEventType());
            }
            layoutBlockList.add(SlackCreator.createLayoutBlock(HeaderBlock.TYPE, eventName.toString()));
        }

        String keptnBridgeDomain = System.getenv(ENV_KEPTN_BRIDGE_DOMAIN);
        if (keptnBridgeDomain == null) {
            LOG.error(ENV_KEPTN_BRIDGE_DOMAIN +" is null!");
        } else {
            Object eventDataObject = event.getData();

            if (eventDataObject instanceof KeptnCloudEventData) {
                KeptnCloudEventData eventData = (KeptnCloudEventData) eventDataObject;
                StringBuilder eventURLSB = new StringBuilder();
                eventURLSB.append(APP_LAYER_PROTOCOL).append("://").append(keptnBridgeDomain);

                if (eventData.getProject() != null) {
                    eventURLSB.append(KEPTN_BRIDGE_PROJECT).append(eventData.getProject()).append("/sequence/");
                    if (!isSequenceEvent && event.getShkeptncontext() != null && event.getId() != null) {
                        eventURLSB.append(event.getShkeptncontext()).append("/event/").append(event.getId());
                    }
                } else {
                    eventURLSB.append(KEPTN_BRIDGE_DASHBOARD);
                }

                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, SlackCreator.formatLink(eventURLSB.toString(),
                        KEPTN_BRIDGE_NAME)));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        }

        return layoutBlockList;
    }
}
