package com.dynatrace.prototype;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnCloudEventParser;
import com.dynatrace.prototype.payloadHandler.KeptnCloudEventHandler;
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

}