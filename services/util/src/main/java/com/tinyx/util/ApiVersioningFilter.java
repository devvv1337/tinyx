package com.tinyx.util;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Provider
public class ApiVersioningFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String version = requestContext.getHeaderString("API-Version");
        if (version == null || !version.equals("v1")) {
            requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid API version").build());
        }
    }
}
