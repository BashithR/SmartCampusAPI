/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

/**
 *
 * @author Bashith Ratnaweera
 */

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.net.URI;

public class Main {

    public static final String BASE_URI = "http://localhost:8080/";

    public static void main(String[] args) throws Exception {
        final ResourceConfig config = new ResourceConfig()
                .packages("com.smartcampus")   // Scan all our packages
                .register(JacksonFeature.class); // Enable JSON

        final HttpServer server = GrizzlyHttpServerFactory
                .createHttpServer(URI.create(BASE_URI), config);

        System.out.println("Smart Campus API started.");
        System.out.println("Access it at: " + BASE_URI + "api/v1");
        System.out.println("Press ENTER to stop...");
        System.in.read();
        server.shutdownNow();
    }
}
