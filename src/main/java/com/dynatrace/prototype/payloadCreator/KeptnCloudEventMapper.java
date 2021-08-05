package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.slack.api.model.block.*;
import com.slack.api.model.block.composition.PlainTextObject;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.slack.api.model.block.composition.BlockCompositions.markdownText;

public abstract class KeptnCloudEventMapper {
    private static final String ENV_KEPTN_BRIDGE_DOMAIN = "KEPTN_BRIDGE_DOMAIN";
    private static final String APP_LAYER_PROTOCOL = "http";
    private static final String KEPTN_BRIDGE_NAME = "Keptn bridge";
    private static final String KEPTN_BRIDGE_DASHBOARD = "/bridge/dashboard";
    private static final String KEPTN_BRIDGE_PROJECT = "/bridge/project";

    /**
     * Returns the specific (relevant / important) data of the given KeptnCloudEvent in a list of LayoutBlock(s).
     * The list can be empty if there was no data to add or an error occurred.
     * @param event to extract
     * @return filled or empty list of LayoutBlock(s)
     */
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        String eventName = event.getType();
        eventName = eventName.replace(KeptnEvent.SH_KEPTN_EVENT.getValue() +".", "");
        eventName = StringUtils.capitalise(eventName);
        eventName = StringUtils.reverse(StringUtils.reverse(eventName).replaceFirst("\\.", " - "));

        if (!eventName.isBlank()) {
            layoutBlockList.add(createSlackBlock(HeaderBlock.TYPE, eventName));
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
                    eventURLSB.append(APP_LAYER_PROTOCOL).append("://").append(keptnBridgeDomain)
                            .append(KEPTN_BRIDGE_PROJECT).append(eventData.getProject()).append("/sequence")
                            .append(event.getShkeptncontext()).append("/event/").append(event.getId());
                }

                layoutBlockList.add(createSlackBlock(SectionBlock.TYPE, formatLink(eventURLSB.toString(), KEPTN_BRIDGE_NAME)));
                layoutBlockList.add(createSlackDividerBlock());
            }
        }

        return layoutBlockList;
    }

    /**
     * Returns key +value if value is not null nor blank, else an empty String ("")
     * @param prefix to insert before value if not null
     * @param value to check
     * @param postfix to insert after value if not null
     * @return key +value or an empty String ("")
     */
    protected String ifNotNull(String prefix, String value, String postfix) {
        StringBuilder resultSB = new StringBuilder();

        if (value != null && !value.isBlank() && !"null".equals(value)) {
            if (prefix != null) {
                resultSB.append(prefix);
            }
            resultSB.append(value);
            if (postfix != null) {
                resultSB.append(postfix);
            }
        }

        return resultSB.toString();
    }

    /**
     * Formats a given link to display only the displayText if send to Slack.
     * Returns the formatted link or null if the link is null.
     * @param link to format
     * @param displayText text which will displayed instead of the link.
     * @return formatted link or null
     */
    protected String formatLink(String link, String displayText) {
        String formattedEmail = null;

        if (link != null && displayText != null) {
            formattedEmail = String.format("<%1$s|%2$s>", link, displayText);
        }

        return formattedEmail;
    }

    /**
     * This method creates a slack block with the given type (if such a block exists).
     * Payload is the text of the block  if it needs one else it will be ignored.
     * @param type of the text (e.g. SectionBlock.TYPE). supported are: SectionBlock, HeaderBlock, DividerBlock
     * @param payload the text if the slack block requires a text
     * @return LayoutBlock or null
     */
    protected LayoutBlock createSlackBlock(String type, String payload) {
        LayoutBlock slackBlock = null;

        if (SectionBlock.TYPE.equals(type)) {
            slackBlock = Blocks.section(section -> section.text(markdownText(payload)));
        } else if (HeaderBlock.TYPE.equals(type)) {
            slackBlock = HeaderBlock.builder().text(PlainTextObject.builder().text(payload).build()).build();
        } else if (DividerBlock.TYPE.equals(type)) {
            slackBlock = DividerBlock.builder().build();
        }
        //TODO: maybe add an ActionBlock (if needed)

        return slackBlock;
    }

    /**
     * This method creates a slack divider block.
     * Returns the slack divider block if successful or else null.
     * @return DividerBlock or null
     */
    protected LayoutBlock createSlackDividerBlock() {
        return createSlackBlock(DividerBlock.TYPE, null);
    }
}
