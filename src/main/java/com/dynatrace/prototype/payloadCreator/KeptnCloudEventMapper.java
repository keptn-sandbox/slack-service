package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.slack.api.model.block.LayoutBlock;
import org.apache.maven.shared.utils.StringUtils;

import java.util.List;

public abstract class KeptnCloudEventMapper {
    protected static final String SERVICE_STAGE_PROJECT_TEXT = "the service '%1$s' from the stage '%2$s' of the project '%3$s'";
    private static final String MESSAGE = "%1$s the %2$s of " +String.format(SERVICE_STAGE_PROJECT_TEXT, "%3$s", "%4$s", "%5$s");
    protected static final String ERROR_NULL_VALUE = "%1$s of %2$s is null!";

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
     * Returns a formatted message for the event if all parameters are not null except the result
     * @param eventResult determined if the message says failed or successful (if null it is successful)
     * @param eventType
     * @param eventName
     * @param service
     * @param stage
     * @param project
     * @return the message if successful or else null
     */
    protected String createMessage(KeptnCloudEventDataResult eventResult, KeptnEvent eventType, KeptnEvent eventName, String service, String stage, String project) {
        String msg = null;

        if (eventType != null && eventName != null && service != null && stage != null && project != null) {
            if (KeptnCloudEventDataResult.FAIL.equals(eventResult)) {
                String eventTypeString = eventType.getValue();

                if (eventTypeString.endsWith("ed")) {
                    eventTypeString = eventTypeString.replace("ed", "");
                }

                msg = "Failed to " +String.format(MESSAGE, eventTypeString, eventName, service, stage, project) +"!";
            } else {
                msg = StringUtils.capitalise(String.format(MESSAGE, eventType, eventName, service, stage, project) +" successfully!");
            }
        }

        return msg;
    }

}
