# Charging Station Agent

Charging Station Agent(CSA) is responsible of communicating with the simulated charging stations in the simulation environment.
CSA holds the status of the 8 charging station in the simulation environment. They are either reserved by a transporter robot or free.

The communication with the simulation environment is established through the websocket communication protocol. 
Followings covers the example of the request and response messages between the CSA and Charging stations in the simulation environment.   

Request message
```json
{
  "requester": {
    "id": "chargingstation-driver-1"
  },
  "requestee": {
    "id": "chargingstation-service"
  },
  "request": {
    "status": "getStatusAll"
  },
  "response": {
    "chargingstation-1": {
      "status": "occupied|non-occupied"
    }
  }
}
```
Response message:
```json
{
  "payload": {
    "requestee": {
      "id": "chargingstation-service"
    },
    "requester": {
      "id": "chargingstation-1"
    },
    "request": {
      "status": "getStatusAll"
    },
    "response": {
      "chargingstationstates": {
        "chargingstation-1": {
          "status": "Reserved"
        },
        "chargingstation-2": {
          "status": "Reserved"
        },
        "chargingstation-3": {
          "status": "Reserved"
        },
        "chargingstation-4": {
          "status": "Reserved"
        },
        "chargingstation-5": {
          "status": "Reserved"
        },
        "chargingstation-6": {
          "status": "Reserved"
        },
        "chargingstation-7": {
          "status": "Reserved"
        },
        "chargingstation-8": {
          "status": "Reserved"
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