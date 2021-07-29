package com.dynatrace.prototype.payloadHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConsolePayloadHandler implements PayloadHandler {
    @Override
    public boolean sendPayload(String payload) {
        System.out.println("'" + payload +"'");

        return true;
    }
}
