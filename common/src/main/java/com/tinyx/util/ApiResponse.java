package com.tinyx.util;

import javax.ws.rs.core.Response;

public class ApiResponse {

    public static Response success(Object entity) {
        return Response.ok(entity).build();
    }

    public static Response error(String message, Response.Status status) {
        return Response.status(status).entity(message).build();
    }
}
