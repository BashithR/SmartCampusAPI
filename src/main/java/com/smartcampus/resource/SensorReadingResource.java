/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

/**
 *
 * @author Bashith Ratnaweera
 */

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final Sensor sensor;
    private final DataStore store = DataStore.getInstance();

    // Constructor receives the specific sensor from SensorResource
    public SensorReadingResource(Sensor sensor) {
        this.sensor = sensor;
    }

    // GET /api/v1/sensors/{id}/readings — Get reading history
    @GET
    public Response getReadings() {
        List<SensorReading> history = store.getReadings()
                .getOrDefault(sensor.getId(), List.of());
        return Response.ok(history).build();
    }

    // POST /api/v1/sensors/{id}/readings — Add a new reading
    @POST
    public Response addReading(SensorReading reading) {
        // Business rule: can't post if sensor is under maintenance
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensor.getId());
        }

        SensorReading newReading = new SensorReading(reading.getValue());
        store.getReadings().get(sensor.getId()).add(newReading);

        // SIDE EFFECT: update parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        return Response.status(201).entity(newReading).build();
    }
}
