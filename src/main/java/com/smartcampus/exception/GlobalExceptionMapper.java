/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author Bashith Ratnaweera
 */

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER =
        Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {

        // IMPORTANT: If it's already a JAX-RS exception (like 404, 405 etc.),
        // let it pass through with its correct status code — don't turn it into 500
        if (e instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) e;
            Response original = wae.getResponse();
            
            // Log it but return the proper status
            LOGGER.warning(String.format("JAX-RS exception: %s", e.getMessage()));
            
            return Response.status(original.getStatus())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of(
                        "status", original.getStatus(),
                        "error", "REQUEST_ERROR",
                        "message", e.getMessage() != null ? e.getMessage() : "Request error"
                    ))
                    .build();
        }

        // For genuine unexpected errors (NullPointerException, etc.) → 500
        LOGGER.log(Level.SEVERE, "Unexpected server error", e);

        return Response.status(500)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                    "status", 500,
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred. Please contact support."
                ))
                .build();
    }
}