package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventData;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventProblemData;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.*;
import com.slack.api.model.block.composition.ConfirmationDialogObject;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.ButtonElement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import static com.slack.api.model.block.composition.BlockCompositions.markdownText;

public class SlackCreator {
    public static final String SLACK_STYLE_PRIMARY = "primary";
    public static final String SLACK_STYLE_DANGER = "danger";
    private static final String COLOR_PASS = "#00FF00";
    private static final String COLOR_WARNING = "#FFFF00";
    private static final String COLOR_FAIL = "#FF0000";

    /**
     * Formats a given link to display only the displayText if send to Slack.
     * Returns the formatted link or null if the link is null.
     * @param link to format
     * @param displayText text which will displayed instead of the link.
     * @return formatted link or null
     */
    public static String formatLink(String link, String displayText) {
        String formattedEmail = null;

        if (link != null && displayText != null) {
            formattedEmail = String.format("<%1$s|%2$s>", link, displayText);
        }

        return formattedEmail;
    }

    /**
     * This method creates a slack divider block.
     * Returns the slack divider block if successful or else null.
     * @return DividerBlock or null
     */
    public static LayoutBlock createDividerBlock() {
        return createLayoutBlock(DividerBlock.TYPE, null);
    }

    /**
     * This method creates a slack block with the given type (if such a block exists).
     * Payload is the text of the block if it needs one else it will be ignored.
     * @param type of the text (e.g. SectionBlock.TYPE).
     *       supported are:
     *          SectionBlock (payload:String),
     *          HeaderBlock (payload:String),
     *          DividerBlock (payload:null),
     *          ActionBlock (payload:List<BlockElement>)
     * @param payload an object representing the text if the slack block requires a text or a list of BlockElements for the ActionBlock
     * @return LayoutBlock or null
     */
    public static LayoutBlock createLayoutBlock(String type, Object payload) {
        LayoutBlock slackBlock = null;

        if (SectionBlock.TYPE.equals(type)) {
            if (payload instanceof String) {
                String message = payload.toString();
                slackBlock = Blocks.section(section -> section.text(markdownText(message)));
            }
        } else if (HeaderBlock.TYPE.equals(type)) {
            if (payload instanceof String) {
                String message = payload.toString();
                slackBlock = HeaderBlock.builder().text(PlainTextObject.builder().text(message).build()).build();
            }
        } else if (DividerBlock.TYPE.equals(type)) {
            slackBlock = DividerBlock.builder().build();
        } else if (ActionsBlock.TYPE.equals(type)) {
            if (payload instanceof List<?>) {
                List<BlockElement> elementsList = (List<BlockElement>) payload;
                slackBlock = ActionsBlock.builder().elements(elementsList).build();
            }
        }

        return slackBlock;
    }

    /**
     * Creates a slack button with the given parameters.
     * Returns the object or throws an exception if something failed.
     * @param text of the button that will be displayed
     * @param value which the button will send
     * @param style of the button: 'primary' or 'danger'
     * @param buttonConfirmation confirmation to display if the button was clicked
     * @return ButtonElement
     */
    public static ButtonElement createButton(String id, String text, String value, String style, ConfirmationDialogObject buttonConfirmation) {
        return ButtonElement.builder()
                .actionId(id + LocalDateTime.now())
                .text(PlainTextObject.builder().text(text).build())
                .value(value)
                .style(style)
                .confirm(buttonConfirmation)
                .build();
    }

    /**
     * Creates a slack confirmation dialog with the given parameters.
     * Returns the object or throws an exception if something failed.
     * @param title to display of the confirmation. plain_text
     * @param text to display of the confirmation. markdown_text
     * @param confirm text of confirm button. plain_text
     * @param deny text of deny button. plain_text
     * @param style of the confirm button: 'primary' or 'danger'
     * @return ConfirmationDialogObject
     */
    public static ConfirmationDialogObject createConfirmationDialog(String title, String text, String confirm, String  deny, String style) {
        return ConfirmationDialogObject.builder()
                .title(PlainTextObject.builder().text(title).build())
                .text(MarkdownTextObject.builder().text(text).build())
                .confirm(PlainTextObject.builder().text(confirm).build())
                .deny(PlainTextObject.builder().text(deny).build())
                .style(style)
                .build();
    }

    /**
     * Creates a list of slack attachments with one attachment with the given list as blocks.
     * Evaluate the result of the keptn event and changes the color of the attachment accordingly.
     *
     * @param event        Keptn Cloud Event to evaluate the result
     * @param layoutBlocks that are added to the attachment
     * @param fallback     of the attachment (e.g. message of notification)
     * @return List<Attachment> with one attachment
     */
    public static List<Attachment> createAttachment(KeptnCloudEvent event, List<LayoutBlock> layoutBlocks, String fallback) {
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        Object eventDataObject = event.getData();

        attachment.setBlocks(layoutBlocks);
        attachment.setFallback(fallback);
        if (eventDataObject instanceof KeptnCloudEventData) {
            KeptnCloudEventData eventData = (KeptnCloudEventData) eventDataObject;

            if (eventData.getResult() != null) {
                attachment.setColor(getEventResultColor(eventData.getResult().getValue()));
            } else if (eventData instanceof KeptnCloudEventProblemData) {
                KeptnCloudEventProblemData eventProblemData = (KeptnCloudEventProblemData) eventData;
                attachment.setColor(getEventResultColor(eventProblemData.getState()));
            }
        }
        attachments.add(attachment);

        return attachments;
    }

    /**
     * Creates a list of slack attachments with one attachment with the given list as blocks.
     * Changes the color of the attachment according to the eventResult.
     *
     * @param eventResult  to set the color accordingly
     * @param layoutBlocks that are added to the attachment
     * @param fallback     of the attachment (e.g. message of notification)
     * @return List<Attachment> with one attachment if successful or else null
     */
    public static List<Attachment> createSlackAttachment(KeptnCloudEventDataResult eventResult, List<LayoutBlock> layoutBlocks, String fallback) {
        List<Attachment> attachments = null;

        if (eventResult != null && layoutBlocks != null && fallback != null) {
            Attachment attachment = new Attachment();

            attachment.setColor(getEventResultColor(eventResult.getValue()));
            attachment.setBlocks(layoutBlocks);
            attachment.setFallback(fallback);

            attachments = new ArrayList<>();
            attachments.add(attachment);
        }

        return attachments;
    }

    private static String getEventResultColor(String result) {
        String eventResultColor = null;

        if (result != null) {
            if (KeptnCloudEventDataResult.PASS.getValue().equals(result) || KeptnCloudEventProblemData.RESOLVED.equals(result)) {
                eventResultColor = COLOR_PASS;
            } else if (KeptnCloudEventDataResult.WARNING.getValue().equals(result)) {
                eventResultColor = COLOR_WARNING;
            } else if (KeptnCloudEventDataResult.FAIL.getValue().equals(result) || KeptnCloudEventProblemData.OPEN.equals(result)) {
                eventResultColor = COLOR_FAIL;
            }
        }

        return eventResultColor;
    }
}
