package com.tinyx.social;

import com.tinyx.dto.SocialDTO;
import com.tinyx.util.ApiResponse;
import com.tinyx.models.Social;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/social")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SocialResource {

    @Inject
    SocialService socialService;

    @POST
    public Response performAction(SocialDTO socialDTO) {
        try {
            Social social = new Social();
            social.setUserId(socialDTO.getUserId());
            social.setTargetUserId(socialDTO.getTargetUserId());
            social.setAction(socialDTO.getAction());
            socialService.performAction(social);
            return ApiResponse.success("Action performed successfully");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/followers/{userId}")
    public Response getFollowers(@PathParam("userId") String userId) {
        List<String> followers = socialService.getFollowers(userId);
        return ApiResponse.success(followers);
    }

    @GET
    @Path("/following/{userId}")
    public Response getFollowing(@PathParam("userId") String userId) {
        List<String> following = socialService.getFollowing(userId);
        return ApiResponse.success(following);
    }

    @GET
    @Path("/blocked/{userId}")
    public Response getBlockedUsers(@PathParam("userId") String userId) {
        List<String> blockedUsers = socialService.getBlockedUsers(userId);
        return ApiResponse.success(blockedUsers);
    }

    @GET
    @Path("/blockers/{userId}")
    public Response getBlockingUsers(@PathParam("userId") String userId) {
        List<String> blockingUsers = socialService.getBlockingUsers(userId);
        return ApiResponse.success(blockingUsers);
    }
}
