package com.tinyx.media;

import com.tinyx.util.ApiResponse;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/media")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MediaResource {

    @Inject
    MediaService mediaService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadMedia(@MultipartForm FileUploadForm form) {
        try {
            String mediaId = mediaService.uploadMedia(form.getFileContent());
            return ApiResponse.success(mediaId);
        } catch (IOException e) {
            return ApiResponse.error(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getMedia(@PathParam("id") String id) {
        try {
            byte[] content = mediaService.getMedia(id);
            return Response.ok(content).build();
        } catch (IOException e) {
            return ApiResponse.error(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
