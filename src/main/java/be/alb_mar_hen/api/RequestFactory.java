package be.alb_mar_hen.api;

import javax.ws.rs.core.Response;

public class RequestFactory {

    // 200 OK
    public static Response createOkResponse(String message) {
        return Response.status(Response.Status.OK).entity(message).build();
    }
    
    public static Response createOkResponse() {
        return Response.status(Response.Status.OK).build();
    }

    // 201 Created
    public static Response createCreatedResponse(String message) {
        return Response.status(Response.Status.CREATED).entity(message).build();
    }

    // 204 No Content
    public static Response createNoContentResponse(String message) {
        return Response.status(Response.Status.NO_CONTENT).entity(message).build();
    }

    // 400 Bad Request
    public static Response createBadRequestResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), message).build();
    }

    // 401 Unauthorized
    public static Response createUnauthorizedResponse(String message) {
        return Response.status(Response.Status.UNAUTHORIZED.getStatusCode(), message).build();
    }

    // 403 Forbidden
    public static Response createForbiddenResponse(String message) {
        return Response.status(Response.Status.FORBIDDEN.getStatusCode(), message).build();
    }

    // 404 Not Found
    public static Response createNotFoundResponse(String message) {
        return Response.status(Response.Status.NOT_FOUND.getStatusCode(), message).build();
    }

    // 500 Internal Server Error
    public static Response createServerErrorResponse(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message).build();
    }
}
