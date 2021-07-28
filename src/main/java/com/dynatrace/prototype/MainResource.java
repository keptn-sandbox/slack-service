package com.dynatrace.prototype;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventParser;
import com.dynatrace.prototype.payloadHandler.PayloadHandler;
import com.dynatrace.prototype.payloadCreator.PayloadCreator;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/quarkus")
public class MainResource {

    @Inject
    private PayloadHandler payloadHandler; //sends / writes the payload to something
    @Inject
    private PayloadCreator payloadCreator; //creates a payload out of a KeptnCloudEvent

    @POST
    @Consumes({MediaType.MEDIA_TYPE_WILDCARD})
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/handleEvent")
    public String handleEvent(String event) {
        String result = "Something wend wrong!";

        try {
            KeptnCloudEvent keptnCloudEvent = KeptnCloudEventParser.parseJsonToKeptnCloudEvent(event);
            String message = payloadCreator.createPayload(keptnCloudEvent);

            if (payloadHandler.sendPayload(message)) {
                result = "Posted message: \"" +message +"\" successfully!";
            }
        } catch (JsonProcessingException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }

}