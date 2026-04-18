# Smart Campus API

A RESTful API for managing campus Rooms and Sensors, built with **JAX-RS (Jersey 3.x)** and an embedded **Grizzly HTTP server**. All data is stored in-memory using `ConcurrentHashMap` and `ArrayList` — no database required.

---

## API Design Overview

The API follows RESTful principles with a clear, versioned resource hierarchy:

```
/api/v1                          → Discovery endpoint
/api/v1/rooms                    → Room collection
/api/v1/rooms/{roomId}           → Individual room
/api/v1/sensors                  → Sensor collection (supports ?type= filtering)
/api/v1/sensors/{sensorId}/readings  → Sub-resource: reading history for a sensor
```

**Key design decisions:**
- All responses are `application/json`
- Resources are nested to reflect the physical campus structure (rooms contain sensors, sensors have readings)
- A shared `DataStore` singleton (using `ConcurrentHashMap`) acts as the in-memory database
- Custom exception mappers ensure no raw stack traces are ever returned to clients
- A logging filter provides observability on every request and response

---

## Technology Stack

| Component | Technology |
|---|---|
| REST Framework | JAX-RS via Jersey 3.1.3 |
| HTTP Server | Grizzly 2 (embedded) |
| JSON | Jackson (via `jersey-media-json-jackson`) |
| Build Tool | Maven |
| Java Version | Java 11 |

---

## How to Build and Run

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Steps

**1. Clone the repository**
```bash
git clone <your-repo-url>
cd SmartCampusAPI
```

**2. Build the fat JAR**
```bash
mvn clean package
```
This produces `target/SmartCampusAPI-1.0-SNAPSHOT.jar` — a self-contained runnable JAR with all dependencies bundled.

**3. Run the server**
```bash
java -jar target/SmartCampusAPI-1.0-SNAPSHOT.jar
```

The server starts at `http://localhost:8080/`. Press **ENTER** in the terminal to stop it.

**4. Verify it's running**
```bash
curl http://localhost:8080/api/v1
```
You should receive a JSON discovery response immediately.

---

## Sample curl Commands

### 1. Discovery — GET /api/v1
Retrieve API metadata and available resource links.
```bash
curl -X GET http://localhost:8080/api/v1
```

### 2. List all Rooms — GET /api/v1/rooms
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### 3. Create a new Room — POST /api/v1/rooms
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "ENG-201", "name": "Engineering Lab", "capacity": 40}'
```

### 4. Create a new Sensor linked to a Room — POST /api/v1/sensors
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "CO2-001", "type": "CO2", "status": "ACTIVE", "currentValue": 412.5, "roomId": "ENG-201"}'
```

### 5. Filter Sensors by type — GET /api/v1/sensors?type=CO2
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

### 6. Post a new Sensor Reading — POST /api/v1/sensors/{sensorId}/readings
```bash
curl -X POST http://localhost:8080/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 430.0}'
```

### 7. Get Reading History for a Sensor — GET /api/v1/sensors/{sensorId}/readings
```bash
curl -X GET http://localhost:8080/api/v1/sensors/CO2-001/readings
```

### 8. Attempt to delete a Room that still has Sensors (triggers 409)
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

### 9. Attempt to register a Sensor with a non-existent roomId (triggers 422)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-999", "type": "Temperature", "status": "ACTIVE", "currentValue": 21.0, "roomId": "FAKE-999"}'
```

### 10. Delete a Room with no Sensors — DELETE /api/v1/rooms/{roomId}
First create a room with no sensors, then delete it:
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "EMPTY-01", "name": "Empty Room", "capacity": 10}'

curl -X DELETE http://localhost:8080/api/v1/rooms/EMPTY-01
```

---

## Project Structure

```
SmartCampusAPI/
└── src/main/java/com/smartcampus/
    ├── Main.java                         # Starts the Grizzly server
    ├── SmartCampusAPI.java               # JAX-RS Application subclass (@ApplicationPath)
    ├── model/
    │   ├── Room.java
    │   ├── Sensor.java
    │   └── SensorReading.java
    ├── resource/
    │   ├── DiscoveryResource.java        # GET /api/v1
    │   ├── RoomResource.java             # GET/POST/DELETE /api/v1/rooms
    │   ├── SensorResource.java           # GET/POST /api/v1/sensors + sub-resource locator
    │   └── SensorReadingResource.java    # GET/POST /api/v1/sensors/{id}/readings
    ├── store/
    │   └── DataStore.java                # Singleton in-memory data store
    ├── exception/
    │   ├── RoomNotEmptyException.java
    │   ├── RoomNotEmptyExceptionMapper.java
    │   ├── LinkedResourceNotFoundException.java
    │   ├── LinkedResourceNotFoundExceptionMapper.java
    │   ├── SensorUnavailableException.java
    │   ├── SensorUnavailableExceptionMapper.java
    │   └── GlobalExceptionMapper.java
    └── filter/
        └── LoggingFilter.java
```
