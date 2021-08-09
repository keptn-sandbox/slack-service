package com.dynatrace.prototype;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventParser;
import com.dynatrace.prototype.payloadHandler.KeptnCloudEventHandler;
import com.dynatrace.prototype.payloadHandler.SlackHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.util.JsonPayloadExtractor;
import com.slack.api.util.json.GsonFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/quarkus")
public class MainResource {

    @Inject
    private KeptnCloudEventHandler keptnCloudEventHandler;

    @POST
    @Consumes({MediaType.MEDIA_TYPE_WILDCARD})
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/handleEvent")
    public String handleEvent(String event) {
        String result = "Something wend wrong!";

        try {
            KeptnCloudEvent keptnCloudEvent = KeptnCloudEventParser.parseJsonToKeptnCloudEvent(event);

            if (keptnCloudEventHandler.handleEvent(keptnCloudEvent)) {
                result = "Posted message successfully!";
            }
        } catch (JsonProcessingException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("/slackUserInput")
    public String handleSlackUserInput(String payload) {
        JsonPayloadExtractor extractor = new JsonPayloadExtractor();
        Gson gson = GsonFactory.createSnakeCase();

        try {
            String jsonPayload = extractor.extractIfExists(payload);
            BlockActionPayload blockActionPayload = gson.fromJson(jsonPayload, BlockActionPayload.class);
            //TODO: need to be changed
            SlackHandler slackHandler = new SlackHandler();
            slackHandler.sendEvent(blockActionPayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}