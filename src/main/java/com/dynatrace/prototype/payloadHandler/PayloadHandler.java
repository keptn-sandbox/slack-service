package com.dynatrace.prototype.payloadHandler;

public interface PayloadHandler {
    /**
     * Handles the given payload by writing it in the Console or send it to for example a chat app.
     * @param payload the payload to handle
     * @return true if it was successful else false
     */
    boolean sendPayload(String payload);
}
