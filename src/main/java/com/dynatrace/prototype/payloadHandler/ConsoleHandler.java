package com.dynatrace.prototype.payloadHandler;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import org.jboss.logging.Logger;

import javax.inject.Singleton;

@Singleton
public class ConsoleHandler implements KeptnCloudEventHandler {
    private static final Logger LOG = Logger.getLogger(ConsoleHandler.class);

    @Override
    public boolean receiveEvent(KeptnCloudEvent event) {
        LOG.info("'" + event +"'");

        return true;
    }

    @Override
    public boolean sendEvent(Object payload) {
        return false;
    }
}
