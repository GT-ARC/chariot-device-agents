# Delivery Tray Sensor

Delivery tray sensor is the part of the warehouse simulation environment in CHARIOT and it designates pyhsically a delivery container which is filled by the autonomous robots in the same environment.
It is a simulated sensor and it is connection is performed through a direct interaction with the simulation environment.
The communication is operated through the websocket and the following message is requested and its response is forwarded to the database and agent.
The number of the available container is 5 and all these containers and their status (occupied or free) are visualized via CHARIOT Monitoring and Maintenance App. 

Request message:

```json
{
    "requester":{"id":"tray-driver-id"},
    "requestee":{"id":"tray-service"},
    "request":{"status":"getStatusOf",
    "type":"delivery-tray"},"response":{}
}
```

Received message:

```json
{
  "payload": {
    "requestee": {
      "id": "delivery-container-service"
    },
    "requester": {
      "id": "delivery-container-driver-id"
    },
    "request": {
      "status": "getStatusOf",
      "type": "delivery-container-tray"
    },
    "response": {
      "deliverycontainerstates": {
        "tray-1": {
          "status": "Occupied"
        },
        "tray-2": {
          "status": "Free"
        },
        "tray-3": {
          "status": "Occupied"
        },
        "tray-4": {
          "status": "Occupied"
        },
        "tray-5": {
          "status": "Free"
        }
      }
    }
  }
}
```
## Usage
1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  

## Contacts

The following persons can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)
- Orhan Can Görür