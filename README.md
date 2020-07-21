# Chariot Aggents

## Charging Station Agent

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
## Color Sensor Agent
The conveyor belt is equipped with a color sensor that detects tree colors red, blue and green.
The color of the passing objects on the conveyor belt can be figured out. 
The integration of this sensor is performed not only via agent but also via CHARIOT RE.

If an agent is used for the communication, then a web socket communication is built between the agent and device.
If RE takes the bridge role between agent and device, then the communication is based on MQTT protocol.

The execution of this agent considering of the aforementioned methods should be easily performed
` mvn clean install` command, after adjusting `resources/config/entity.xml` file, in which the path of the agent is given.


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

## Current Sensor Agent
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

## Delivery Robot Agent

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
## Delivery Tray Sensor

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
## DHT11 Sensor Agent

DHT11 sensor measures the temperature and humidity of the surrounding environment. The role of this agent 
is to retrieve those measurements and save in the CHARIOT database, which is then monitored by the 
CHARIOT Monitoring and Maintenance (MM) application.

DHT11 physical sensor is connected with a raspberry Pi and it is connected to this agent through the KURA Runtime Environment.
The representing agent can access DHT11 device over MQTT broker and receives the measurements sent
by the RE with the device UUID topic. Note that the UUID in the sensor agent and the UUID in device that communites with the Kura RE must be same.

MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.

## DHT22 Sensor Agent

DHT22 sensor measures the temperature and humidity of the surrounding environment. The role of this agent 
is to retrieve those measurements and save in the CHARIOT database, which is then monitored by the 
CHARIOT Monitoring and Maintenance (MM) application.

DHT22 physical sensor is connected with a raspberry Pi and it is connected to this agent through the KURA Runtime Environment.
The representing agent can access DHT22 device over MQTT broker and receives the measurements sent
by the RE with the device UUID topic. Note that the UUID in the sensor agent and the UUID in device that communites with the Kura RE must be same.

MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.

## Distance - Proximity Sensor Agent

Proximity Sensor (HRC4) measures the distance between the sensor and the target object placed in the front of the sensor. 
The role of this agent is to retrieve those measurements and save in the CHARIOT database, which is then monitored by the 
CHARIOT Monitoring and Maintenance (MM) application.

HRC4 physical sensor is connected with a raspberry Pi and it is connected to this agent through the KURA Runtime Environment.
The representing agent can access HRC4 device over MQTT broker and receives the measurements sent by the RE with the device UUID topic. 
Note that the UUID in the sensor agent and the UUID in device that communicates with the Kura RE must be same.

MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.

## ds18b20 Sensor Agent

ds18b20 sensor measures the temperature of the surrounding environment. The role of this agent 
is to retrieve those measurements and save in the CHARIOT database, which is then monitored by the 
CHARIOT Monitoring and Maintenance (MM) application.

ds18b20 physical sensor is connected with a raspberry Pi and it is connected to this agent through the KURA Runtime Environment (RE).
The representing agent can access ds18b20 device over MQTT broker and receives the measurements sent
by the RE with the device UUID topic. 

Note that the UUID in the sensor agent and the UUID in device that communicates with the Kura RE must be same. 
MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.

## Infrared Sensor Agent

Infrared sensor is part of the CHARIOT testbed and one of the conveyor belt pieces. It is responsible for running the conveyor belt
when a product is detected on the conveyor belt. As soon as an object is detected in the front of the sensor, this data is shared 
with this agent and then saved in the database.
The infrared sensor itself is not capable of executing an agent on it due to the resource-constraint structure. It can be connected either  with a powerful on which this agent can run
or through a runtime environment (RE). These two approaches are implemented, and it can used according to the deployment environment. The direct agent connection requires a websocket communication handling, whereas 
the connection via an RE needs MQTT comm. protocol. 

Note that the UUID in the sensor agent and the UUID in device that communicates with an RE must be same. 
MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.


## Infrared Temperature Sensor Agent

Infrared sensor is part of the CHARIOT testbed and one of the conveyor belt pieces. It is responsible for running the conveyor belt
when a product is detected on the conveyor belt. As soon as an object is detected in the front of the sensor, this data is shared 
with this agent and then saved in the database.

Infrared temperature sensor can connect to its agent either directly or through a runtime environment (RE) as many other agents. 
These approaches are both implemented, and it can be choosen according to the deployment environment. The direct agent connection requires a websocket communication handling, whereas 
the connection via an RE needs MQTT comm. protocol. 

Note that the UUID in the sensor agent and the UUID in device that communicates with an RE must be same. 
MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.

## Input Tray Sensor

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
## Robot Arm Actuator
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
## Storage Tray Sensor

Storage tray sensor is the part of the warehouse simulation environment in CHARIOT and it designates pyhsically a storage container which is filled by the autonomous robots in the same environment.
It is a simulated sensor and it is connection is performed through a direct interaction with the simulation environment.
The communication is operated through the websocket and the following message is requested and its response is forwarded to the database and agent.
The number of the available container is 19 and all these containers and their status (occupied or free) are visualized via CHARIOT Monitoring and Maintenance App. 

Request message:

```json
{
    "requester":{"id":"tray-driver-id"},
    "requestee":{"id":"tray-service"},
    "request":{"status":"getStatusOf",
    "type":"storage-tray"},"response":{}
}
```

Received message:

```json
{
  "payload": {
    "requestee": {
      "id": "storage-service"
    },
    "requester": {
      "id": "storage-driver-id"
    },
    "request": {
      "status": "getStatusOf",
      "type": "storage-tray"
    },
    "response": {
      "storagestates": {
        "tray-1": {
          "status": "Occupied"
        },
        "tray-2": {
          "status": "Free"
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
        },
        "tray-9": {
          "status": "Occupied"
        },
        "tray-10": {
          "status": "Occupied"
        },
        "tray-11": {
          "status": "Free"
        },
        "tray-12": {
          "status": "Free"
        },
        "tray-13": {
          "status": "Occupied"
        },
        "tray-14": {
          "status": "Occupied"
        },
        "tray-15": {
          "status": "Occupied"
        },
        "tray-16": {
          "status": "Free"
        },
        "tray-17": {
          "status": "Occupied"
        },
        "tray-18": {
          "status": "Free"
        },
        "tray-19": {
          "status": "Free"
        }
      }
    }
  }
}
```
## 3D Printer Actuator Agent

This agent contains the most important functions that enable to interact with 3D Printer controlled by [OctoPrint](https://octoprint.org/) 
3D Printer Agent embeds an OctoPrint API at [https://robweber.github.io/octoprint-java-lib/](https://robweber.github.io/octoprint-java-lib/), which is a simple wrapper to provide Java-bad communication to an OcroPrint Server using the [REST API](http://docs.octoprint.org/en/master/api/)
This wrapper is partially extended not only to broaden  its function set but also to satisfy the requirement of the CHARIOT demonstrator environment.
 
OctoPrint API establishes the communication using HTTP protocol with OctoPrint server and receives all commands. 
The status information requested by this agent are obtained with the same way. 

The available functions in 3D Printer agent can be extended according to the requirements as long as OctoPrint Server supports. For the sake of the simplicity,
only the most required functions are called in the agent implementation.

## How to run agents

1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  


## Contacts

The following persons can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)
- Orhan Can Görür
