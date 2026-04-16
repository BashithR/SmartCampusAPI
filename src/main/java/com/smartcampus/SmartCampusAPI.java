/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.smartcampus;

/**
 *
 * @author Bashith Ratnaweera
 */
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class SmartCampusAPI extends Application {
    // JAX-RS auto-discovers @Provider and @Path classes
    // No code needed here the annotation will do the work 
}