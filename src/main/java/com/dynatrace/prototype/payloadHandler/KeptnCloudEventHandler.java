package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEvent;

public interface KeptnCloudEventHandler {
    /**
     * Handles the given KeptnCloudEvent. That could be filtering of event specific information and / or writing it in
     * the console or send it to for example to a chat app.
     * @param event is the KeptnCloudEvent to handle.
     * @return true if it was successful or else false
     */
    boolean receiveEvent(KeptnCloudEvent event);

    /**
     * Handles the given payload object and sends a KeptnCloudEvent to Keptn.
     * @param payload to handle
     * @return true if it was successful or else false
     */
    boolean sendEvent(Object payload);
}
