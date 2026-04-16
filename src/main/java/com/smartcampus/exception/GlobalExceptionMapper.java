/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author Bashith Ratnaweera
 */

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = 
        Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        // Log the real error internally (not exposed to client)
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