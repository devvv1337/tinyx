
package com.tinyx.timeline;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/timeline/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserTimelineResource {

    @Inject
    UserTimelineService userTimelineService;

    @GET
    @Path("/{userId}")
    public Response getUserTimeline(@PathParam("userId") String userId,
                                    @QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("10") int size) {
        List<String> timeline = userTimelineService.getUserTimeline(userId, page, size);
        return Response.ok(timeline).build();
    }

    @GET
    @Path("/{userId}/liked")
    public Response getUserLikedPosts(@PathParam("userId") String userId,
                                      @QueryParam("page") @DefaultValue("0") int page,
                                      @QueryParam("size") @DefaultValue("10") int size) {
        List<String> likedPosts = userTimelineService.getUserLikedPosts(userId, page, size);
        return Response.ok(likedPosts).build();
    }
}
