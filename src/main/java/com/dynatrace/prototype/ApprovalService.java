package com.dynatrace.prototype;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@RegisterRestClient
public interface ApprovalService {

    @POST
    @Path("/events") //TODO: check if it is /events
    @Consumes("application/cloudevents+json")
    @Produces(MediaType.TEXT_PLAIN) //TODO: check what it produces
    void sentApprovalFinished(KeptnCloudEvent event);

}
