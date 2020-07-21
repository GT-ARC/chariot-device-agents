# Robot Arm Actuator
The purpose of this actuator is to interact with a robot arm, which is capable of doing complex tasks such as pick&place the box on the conveyor belt.
Robot Arm is of course supports more operations, however, the focus is here to observe the behaviour and triggering a move action at the robot arm.
The communication is performed via websocket with a ROS environment. The number of the operations in this agent can be extended in case of need, as long as the matching
functions are available on the robot side. 
The following request message with the initial configuration is sent to the robot arm and a response is sent back by the robot arm.

**Request Messages**
```json 
{
  "requester": {
    "id": "dobotarm-driver"
  },
  "requestee": {
    "id": "dobotarm-service"
  },
  "request": {
    "switch": "on",
    "speed": "",
    "calibration": {
      "pickX": "264.11",
      "pickY": "52.79",
      "pickZ": "12.72",
      "placeX": "244.50",
      "placeY": "-206.10",
      "placeZ": "-35.85",
      "placeInt": "90",
      "placeIntZ": "26"
    },
    "placeRules": {
      "placeLocR": "0",
      "placeLocG": "1",
      "placeLocB": "2",
      "boxLimR": "3",
      "boxLimG": "3",
      "boxLimB": "3",
      "storageLim": "3"
    },
    "humanInteraction": "false"
  },
  "response": {}
}
```

**Response message**

```json
{
  "requester": {
    "id": "dobotarm-driver"
  },
  "requestee": {
    "id": "dobotarm-service"
  },
  "request": {
  },
  "response": {
    "dobotarm-service": {
      "status": "on",
      "speed": "currentValue",
      "calibration": "true",
      "placeRules": {
        "placeLocR": "0",
        "placeLocG": "0",
        "placeLocB": "2",
        "boxLimR": "3",
        "boxLimG": "3",
        "boxLimB": "3",
        "storageLim": "3"
      },
      "humanInteraction": "false"
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