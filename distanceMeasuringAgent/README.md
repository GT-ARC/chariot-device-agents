# Distance - Proximity Sensor Agent

Proximity Sensor (HRC4) measures the distance between the sensor and the target object placed in the front of the sensor. 
The role of this agent is to retrieve those measurements and save in the CHARIOT database, which is then monitored by the 
CHARIOT Monitoring and Maintenance (MM) application.

HRC4 physical sensor is connected with a raspberry Pi and it is connected to this agent through the KURA Runtime Environment.
The representing agent can access HRC4 device over MQTT broker and receives the measurements sent by the RE with the device UUID topic. 
Note that the UUID in the sensor agent and the UUID in device that communicates with the Kura RE must be same.

MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.


## Usage
1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  

## Contacts

The following persons can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)