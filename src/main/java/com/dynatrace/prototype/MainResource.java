package com.dynatrace.prototype;

import com.dynatrace.prototype.domainModel.KeptnCloudEventParser;
import com.dynatrace.prototype.domainModel.keptnCloudEvents.KeptnCloudEvent;
import com.dynatrace.prototype.payloadHandler.KeptnCloudEventHandler;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.util.JsonPayloadExtractor;
import com.slack.api.util.json.GsonFactory;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/quarkus")
public class MainResource {
    private static final Logger LOG = Logger.getLogger(MainResource.class);

    @Inject
    private KeptnCloudEventHandler keptnCloudEventHandler;

    @POST
    @Consumes({MediaType.MEDIA_TYPE_WILDCARD})
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/handleEvent")
    public String receiveEvent(String event) {
        String result = "Something went wrong!";

        try {
            KeptnCloudEvent keptnCloudEvent = KeptnCloudEventParser.parseJsonToKeptnCloudEvent(event);

            if (keptnCloudEventHandler.receiveEvent(keptnCloudEvent)) {
                result = "Received event successfully!";
            }
        } catch (JsonSyntaxException e) {
            LOG.error("An exception occurred while handling an event from Keptn!", e);
        }

        return result;
    }

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("/sendEvent")
    public String sendEvent(String payload) {
        String result = "Something went wrong!";
        JsonPayloadExtractor extractor = new JsonPayloadExtractor();
        Gson gson = GsonFactory.createSnakeCase();

        try {
            String jsonPayload = extractor.extractIfExists(payload);
            BlockActionPayload blockActionPayload = gson.fromJson(jsonPayload, BlockActionPayload.class);

            if (keptnCloudEventHandler.sendEvent(blockActionPayload)) {
                result = "Send event successfully!";
            }
        } catch (Exception e) {
            LOG.error("An exception occurred while sending an event to Keptn!", e);
        }

        return result;
    }

}
