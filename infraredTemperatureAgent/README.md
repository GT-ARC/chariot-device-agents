# Infrared Temperature Sensor Agent

Infrared sensor is part of the CHARIOT testbed and one of the conveyor belt pieces. It is responsible for running the conveyor belt
when a product is detected on the conveyor belt. As soon as an object is detected in the front of the sensor, this data is shared 
with this agent and then saved in the database.

Infrared temperature sensor can connect to its agent either directly or through a runtime environment (RE) as many other agents. 
These approaches are both implemented, and it can be choosen according to the deployment environment. The direct agent connection requires a websocket communication handling, whereas 
the connection via an RE needs MQTT comm. protocol. 

Note that the UUID in the sensor agent and the UUID in device that communicates with an RE must be same. 
MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.


## Usage
1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  

## Contacts

The following persons can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)
- Orhan Can Görür