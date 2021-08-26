package com.dynatrace.prototype.payloadCreator;

import com.slack.api.model.block.ActionsBlock;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.BlockElements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SlackCreatorTest {
    private static DividerBlock dividerBlock;

    @BeforeAll
    static void init() {
        dividerBlock = DividerBlock.builder().build();
    }

    @Test
    void testWorkingFormatLink() {
        assertEquals("<a|b>", SlackCreator.formatLink("a","b"));
        assertNull(SlackCreator.formatLink("a", null));
        assertNull(SlackCreator.formatLink(null, "b"));
        assertNull(SlackCreator.formatLink(null, null));
    }

    @Test
    void testCreateDividerBlock() {
        assertEquals(dividerBlock, SlackCreator.createDividerBlock());
    }

    @Test
    void testCreateLayoutBlock() {
        MarkdownTextObject markdownText = MarkdownTextObject.builder().text("").build();
        PlainTextObject plainText = PlainTextObject.builder().text("").build();

        SectionBlock sectionBlock = SectionBlock.builder().text(markdownText).build();
        HeaderBlock headerBlock = HeaderBlock.builder().text(plainText).build();
        ActionsBlock actionsBlock = ActionsBlock.builder().build();

        assertEquals(sectionBlock, SlackCreator.createLayoutBlock(SectionBlock.TYPE, ""));
        assertNull(SlackCreator.createLayoutBlock(SectionBlock.TYPE, null));
        assertNull(SlackCreator.createLayoutBlock(SectionBlock.TYPE, 0));

        assertEquals(headerBlock, SlackCreator.createLayoutBlock(HeaderBlock.TYPE, ""));
        assertNull(SlackCreator.createLayoutBlock(HeaderBlock.TYPE, null));
        assertNull(SlackCreator.createLayoutBlock(HeaderBlock.TYPE, 0));

        assertEquals(dividerBlock, SlackCreator.createLayoutBlock(DividerBlock.TYPE, ""));
        assertEquals(dividerBlock, SlackCreator.createLayoutBlock(DividerBlock.TYPE, null));

        assertEquals(actionsBlock, SlackCreator.createLayoutBlock(ActionsBlock.TYPE, new ArrayList<BlockElements>()));
        assertNull(SlackCreator.createLayoutBlock(ActionsBlock.TYPE, new ArrayList<String>().add("a")));
        assertNull(SlackCreator.createLayoutBlock(ActionsBlock.TYPE, null));
        assertNull(SlackCreator.createLayoutBlock(ActionsBlock.TYPE, ""));

        assertNull(SlackCreator.createLayoutBlock("a", "b"));
        assertNull(SlackCreator.createLayoutBlock("a", null));
        assertNull(SlackCreator.createLayoutBlock(null, null));
    }

}
