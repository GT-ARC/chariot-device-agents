## Conveyor Belt Actuator

Conveyor belt is the part of the CHARIOT smart factory testbed. The purpose of this agent is to communicate with this conveyor belt device. Apart from the real testbed environment
CHARIOT provides also a simulated smart factory environment, in which a simulated conveyor belt is placed. This agent can also communicate with it in case the communciation is set up accurately. 

Conveyor Belt Agent has a direct connection to device, since the physical device has enough capacity to run it. The communication is based on the websocket and the following request message and the response.  

**Request message**
```json
{
    "requester": {
        "id": "simulated-conveyor-driver"
    },
    "requestee": {
        "id": "conveyor-service-1"      // from 1 to ...
    },
    "request": {      // for NO torque and speed request, leave them empty, "".
        "switch": "on",
        "speed": {
            "contSpeedReq": {     // leave this empty for oneTime speed req.
                "starting_speed": "5.0",
                "ending_speed": "35.0",
                "numberofsteps": "30.0",
                "step_time": "2.0"
            },
            "oneTimeSpeedReq": "35.0"   // leave this empty for continous speed reqs.
        },
        "motorData": "",
        "torqueRequest": {      // abnormal is for maintenance tests
            "abnormal":{
              "type": "log",    // others: exp, ramp, step
              "param1": "0.1",  // torque = -log(param1*x + param2)
              "param2": "1.0",
              "holdTime": "3.0"
            },
            "normal": "0.2" // a normal load installed on the conveyor
        }
    },
    "response": {
    }
}
```
**Response message**
```json
{
    "requester": {
        "id": "simulated-conveyor-driver"
    },
    "requestee": {
        "id": "conveyor-service-1"      // from 1 to ...
    },
    "request": {      // for NO torque and speed request, leave them empty, "".
        "switch": "on",
        "speed": {
            "contSpeedReq": {     // leave this empty for oneTime speed req.
                "starting_speed": "5.0",
                "ending_speed": "35.0",
                "numberofsteps": "30.0",
                "step_time": "2.0"
            },
            "oneTimeSpeedReq": "35.0"   // leave this empty for continous speed reqs.
        },
        "motorData": "",
        "torqueRequest": {      // abnormal is for maintenance tests
            "abnormal":{
              "type": "log",    // others: exp, ramp, step
              "param1": "0.1",  // torque = -log(param1*x + param2)
              "param2": "1.0",
              "holdTime": "3.0"
            },
            "normal": "0.2" // a normal load installed on the conveyor
        }
    },
    "response": {
        "conveyor-service-1": {   // from 1 to ...
            "status": "on",
            "speed": "53.6444366027839", // the same as under motorData
            "motorData": {
              "sensor": "http://10.0.2.83:8001/sensor/1/",
              "log_list_id": 0,
              "timestamp": 13.363,
              "acceleration": 0.027874690631694,
              "speed": 53.6444366027839,
              "temperature": 50.0,
              "current": 0.221145663484359,
              "power_in": 1.98004988138504,
              "load_torque": 0.0
            }
        }
    }
}
```

Conveyor belt is an actuator and it offers some features that can be controlled. `handlePropery` is the function that can be called if a property of the conveyor belt is to be modified.
The first message indicates how the conveyor belt speed changes, whereas the second message manipulates the torque behavior. 

**Proxy Message** 
```json
{
   "command":"property-state-change",
   "uuid":"b1f114f5-f47f-449d-b579-6605bba06110",
   "inputs":{
      "contSpeedReq":"{\"starting_speed\":5,\"ending_speed\":35,\"numberofsteps\":30,\"step_time\":2,\"oneTimeSpeedReq\":25}"
   }
}
```
```json
{
   "command":"property-state-change",
   "uuid":"b1f114f5-f47f-449d-b579-6605bba06110",
   "inputs":{
      "torqueRequest":"{\"torque_behavior\":\"abnormal\",\"type\":\"log\",\"param1\":0.1,\"param2\":1,\"holdTime\":3}"
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