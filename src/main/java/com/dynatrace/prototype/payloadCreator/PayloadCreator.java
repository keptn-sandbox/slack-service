package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;

public interface PayloadCreator {
    /**
     * Creates a payload out of the given KeptnCloudEvent. Contains the information filter and optional formatting.
     * @param event KeptnCloudEvent to create the payload out of.
     * @return the payload string of the event.
     */
    String createPayload(KeptnCloudEvent event);
}
