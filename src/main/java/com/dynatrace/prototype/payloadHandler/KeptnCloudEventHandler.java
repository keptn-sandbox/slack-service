package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;

public interface KeptnCloudEventHandler {
    /**
     * Handles the given KeptnCloudEvent. That could be filtering of event specific information and / or writing it in the console or send it to for example to a chat app.
     * @param event is the KeptnCloudEvent to handle.
     * @return true if it was successful or else false
     */
    boolean handleEvent(KeptnCloudEvent event);
}
