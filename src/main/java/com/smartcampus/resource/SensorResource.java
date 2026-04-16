/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

/**
 *
 * @author Bashith Ratnaweera
 */

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    // GET /api/v1/sensors OR /api/v1/sensors?type=CO2
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>(store.getSensors().values());

        if (type != null && !type.isBlank()) {
            result = result.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        return Response.ok(result).build();
    }

    // POST /api/v1/sensors — Register a new sensor
    @POST
    public Response createSensor(Sensor sensor) {
        // Validate that the roomId exists
        if (sensor.getRoomId() == null || 
            !store.getRooms().containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(sensor.getRoomId());
        }

        if (store.getSensors().containsKey(sensor.getId())) {
            return Response.status(409)
                    .entity(Map.of("error", "Sensor ID already exists."))
                    .build();
        }

        store.getSensors().put(sensor.getId(), sensor);
        store.getReadings().put(sensor.getId(), new ArrayList<>());

        // Link sensor to its room
        Room room = store.getRooms().get(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());

        return Response.status(201).entity(sensor).build();
    }

    // Sub-resource locator — delegates /sensors/{sensorId}/readings
    // to SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(
            @PathParam("sensorId") String sensorId) {

        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }
        return new SensorReadingResource(sensor);
    }
}
