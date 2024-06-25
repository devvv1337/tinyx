package com.tinyx.search;

import com.tinyx.util.ApiResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/search")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    @Inject
    SearchService searchService;

    @GET
    @Path("/posts")
    public Response searchPosts(@QueryParam("query") String query) {
        List<String> results = searchService.searchPosts(query);
        return ApiResponse.success(results);
    }

    @GET
    @Path("/users")
    public Response searchUsers(@QueryParam("query") String query) {
        List<String> results = searchService.searchUsers(query);
        return ApiResponse.success(results);
    }
}
