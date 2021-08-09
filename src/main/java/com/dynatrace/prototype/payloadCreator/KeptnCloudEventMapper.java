package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.slack.api.model.block.*;
import com.slack.api.model.block.composition.ConfirmationDialogObject;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.ButtonElement;

import java.util.List;

import static com.slack.api.model.block.composition.BlockCompositions.markdownText;

public abstract class KeptnCloudEventMapper {
    protected static final String SLACK_STYLE_PRIMARY = "primary";
    protected static final String SLACK_STYLE_DANGER = "danger";

    /**
     * Returns the specific (relevant / important) data of the given KeptnCloudEvent in a list of LayoutBlock(s).
     * The list can be empty if there was no data to add or an error occurred.
     * @param event to extract
     * @return filled or empty list of LayoutBlock(s)
     */
    public abstract List<LayoutBlock> getSpecificData(KeptnCloudEvent event);

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
     * This method creates a slack divider block.
     * Returns the slack divider block if successful or else null.
     * @return DividerBlock or null
     */
    protected LayoutBlock createSlackDividerBlock() {
        return createSlackBlock(DividerBlock.TYPE, null);
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
    protected LayoutBlock createSlackBlock(String type, Object payload) {
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
    protected ButtonElement createSlackButton(String text, String value, String style, ConfirmationDialogObject buttonConfirmation) {
        return ButtonElement.builder()
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
    protected ConfirmationDialogObject createSlackConfirmationDialog(String title, String text, String confirm, String  deny, String style) {
        return ConfirmationDialogObject.builder()
                .title(PlainTextObject.builder().text(title).build())
                .text(MarkdownTextObject.builder().text(text).build())
                .confirm(PlainTextObject.builder().text(confirm).build())
                .deny(PlainTextObject.builder().text(deny).build())
                .style(style)
                .build();
    }

}
