# Delivery Robot Agent

Delivery robot agent represents the delivery robot sensor inside the autonomous simulated smart factory environment.
The following message indicates the request and response messages between agent and simulated delivery robot agent.

Request message

```json
{
  "payload": {
    "requestee": {
      "id": "deliveryrobot-service"
    },
    "requester": {
      "id": "deliveryrobot-driver-2"
    },
    "request": {
      "status": "getStatusAll"
    },
    "response": "{}"
  }
}
```

Received message

```json
{
  "payload": {
    "requestee": {
      "id": "deliveryrobot-service"
    },
    "requester": {
      "id": "deliveryrobot-driver-2"
    },
    "request": {
      "status": "getStatusAll"
    },
    "response": {
      "deliveryrobot-2": {
        "batteryLevel": "100.000000"
      }
    }
  }
}
```

## usage
1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  

## Contacts

The following persons can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)
- Orhan Can Görür