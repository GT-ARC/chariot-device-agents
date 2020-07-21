# Input Tray Sensor

Input tray sensor is the part of the warehouse simulation environment in CHARIOT and it designates pyhsically an input container which is filled by the autonomous robots in the same environment.
It is a simulated sensor and it is connection is performed through a direct interaction with the simulation environment.
The communication is operated through the websocket and the following message is requested and its response is forwarded to the database and agent.

Request message:

```json
{
    "requester":{"id":"tray-driver-id"},
    "requestee":{"id":"tray-service"},
    "request":{"status":"getStatusOf",
    "type":"input-tray"},"response":{}
}
```

Received message:

```json
{
  "payload": {
    "requestee": {
      "id": "input-container-service"
    },
    "requester": {
      "id": "input-container-driver-id"
    },
    "request": {
      "status": "getStatusOf",
      "type": "input-container-tray"
    },
    "response": {
      "inputcontainerstates": {
        "tray-1": {
          "status": "Free"
        },
        "tray-2": {
          "status": "Occupied"
        },
        "tray-3": {
          "status": "Free"
        },
        "tray-4": {
          "status": "Free"
        },
        "tray-5": {
          "status": "Free"
        },
        "tray-6": {
          "status": "Free"
        },
        "tray-7": {
          "status": "Free"
        },
        "tray-8": {
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