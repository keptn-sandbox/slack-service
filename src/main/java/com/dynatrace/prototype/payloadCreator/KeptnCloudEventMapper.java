package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.slack.api.model.block.LayoutBlock;

import java.util.List;

public abstract class KeptnCloudEventMapper {

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

}
