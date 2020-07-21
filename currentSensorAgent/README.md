# Current Sensor Agent
Current sensor is part of the conveyor belt motor and reflects the real time current usage. Current sensor agent is responsible for receiving this measurement and store it in the database.
Like color sensor, this agent can access to the current sensor in 2 ways, namely, over an CHARIOT runtime environment or a direct connection to the device through the device communciation protocol.

- Direct interaction with the sensor over websocket communication protocol
- Interaction through a runtime environment over mqtt communication protocol

Request message
```json
{
    "requester":{"id":"conveyerbelt-driver-id"},
    "requestee":{"id":"conveyerbelt-service"},
    "request":{"switch":"on/off","speed":"0.020 - 1"},
    "response":{
        "conveyer-belt-id":{"status":"on/off","speed":"currentValue"}
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