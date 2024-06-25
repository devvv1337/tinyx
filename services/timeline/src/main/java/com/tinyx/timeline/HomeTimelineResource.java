package com.tinyx.timeline;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/timeline/home")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HomeTimelineResource {

    @Inject
    HomeTimelineService homeTimelineService;

    @GET
    @Path("/{userId}")
    public Response getHomeTimeline(@PathParam("userId") String userId, 
                                    @QueryParam("page") @DefaultValue("0") int page, 
                                    @QueryParam("size") @DefaultValue("10") int size) {
        List<String> timeline = homeTimelineService.getHomeTimeline(userId, page, size);
        return Response.ok(timeline).build();
    }
}
