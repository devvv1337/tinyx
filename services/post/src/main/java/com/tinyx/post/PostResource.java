package com.tinyx.post;

import com.tinyx.dto.PostDTO;
import com.tinyx.util.ApiResponse;
import com.tinyx.util.DtoConverter;
import com.tinyx.models.PostEntity;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    PostService postService;

    @POST
    public Response createPost(@Valid @NotNull PostDTO postDTO) {
        try {
            PostEntity post = DtoConverter.toPostEntity(postDTO);
            postService.createPost(post);
            return ApiResponse.success(DtoConverter.toPostDTO(post));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/{id}")
    public Response getPost(@PathParam("id") String id) {
        PostEntity post = postService.getPost(id);
        return post != null ? ApiResponse.success(DtoConverter.toPostDTO(post)) : ApiResponse.error("Post not found", Response.Status.NOT_FOUND);
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserPosts(@PathParam("userId") String userId, @QueryParam("page") @DefaultValue("0") int page, @QueryParam("size") @DefaultValue("10") int size) {
        List<PostEntity> posts = postService.getUserPosts(userId, page, size);
        List<PostDTO> postDTOs = posts.stream().map(DtoConverter::toPostDTO).collect(Collectors.toList());
        return ApiResponse.success(postDTOs);
    }

    @DELETE
    @Path("/{id}")
    public Response deletePost(@PathParam("id") String id) {
        postService.deletePost(id);
        return ApiResponse.success("Post deleted successfully");
    }
}
