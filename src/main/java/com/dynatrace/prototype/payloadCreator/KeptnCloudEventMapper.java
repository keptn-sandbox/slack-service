package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.slack.api.model.block.LayoutBlock;
import org.apache.maven.shared.utils.StringUtils;
import org.jboss.logging.Logger;

import java.util.List;

public abstract class KeptnCloudEventMapper {
    private static final Logger LOG = Logger.getLogger(KeptnCloudEventMapper.class);
    protected static final String SERVICE_STAGE_PROJECT_TEXT = "the service *'%1$s'* from the stage *'%2$s'* of the project *'%3$s'*";
    private static final String MESSAGE = "*%1$s* the *%2$s* of " + String.format(SERVICE_STAGE_PROJECT_TEXT, "%3$s", "%4$s", "%5$s");
    protected static final String ERROR_NULL_VALUE = "%1$s of %2$s is null!";
    protected static final String WARNING_EVENT_DATA = "EventData is not an instance of '%1$s' although the event type is '%2$s'!";

    /**
     * Returns the specific (relevant / important) data of the given KeptnCloudEvent in a list of LayoutBlock(s).
     * The list can be empty if there was no data to add or an error occurred.
     *
     * @param event to extract
     * @return filled or empty list of LayoutBlock(s)
     */
    public abstract List<LayoutBlock> getSpecificData(KeptnCloudEvent event);

    /**
     * Returns key +value if value is not null nor blank, else an empty String ("")
     *
     * @param prefix  to insert before value if not null
     * @param value   to check
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
     *
     * @param eventResult determined if the message says failed or successful (if null it is successful)
     * @param eventType
     * @param eventName
     * @param service
     * @param stage
     * @param project
     * @return the message if successful, otherwise an empty String
     */
    protected String createMessage(KeptnCloudEventDataResult eventResult, KeptnEvent eventType, String eventName, String service, String stage, String project) {
        String msg = "";

        if (eventType != null && eventName != null && service != null && stage != null && project != null) {
            if (KeptnCloudEventDataResult.FAIL.equals(eventResult)) {
                String eventTypeString = eventType.getValue();

                if (eventTypeString.endsWith("ed")) {
                    eventTypeString = eventTypeString.replace("ed", "");
                }

                msg = "Failed to " + String.format(MESSAGE, eventTypeString, eventName, service, stage, project) + "!";
            } else {
                msg = String.format(MESSAGE, StringUtils.capitalise(eventType.getValue()), eventName, service, stage, project) + " successfully!";
            }
        }

        return msg;
    }

    /**
     * Logs an error if the given value is null.
     * Returns true if the value is null, otherwise false
     *
     * @param value     to check if its null
     * @param valueName name of the given value (for specific error log message)
     * @param eventName name of the event the value is from (for specific error log message)
     * @return true if value is null, otherwise false
     */
    protected boolean logErrorIfNull(String value, String valueName, String eventName) {
        boolean isNull = false;

        if (value == null) {
            LOG.errorf(ERROR_NULL_VALUE, valueName, eventName);
            isNull = true;
        }

        return isNull;
    }

}
