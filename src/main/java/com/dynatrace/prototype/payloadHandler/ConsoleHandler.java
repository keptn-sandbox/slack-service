package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConsoleHandler implements KeptnCloudEventHandler {
    @Override
    public boolean handleEvent(KeptnCloudEvent event) {
        System.out.println("'" + event +"'");

        return true;
    }
}
