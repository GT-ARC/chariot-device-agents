## hx711 Load Sensor Agent

hx711 sensor quantifies the weights of the objects placed on it. The sensor is connected via websocket protocol and represented by the load sensor agent.
The developed system is composed of three load sensors, therefore three similar agents using different websocket endpoints
are available under this folder. In order to not be redundant, a script can be written for the distinct parameters in these agents.

Load sensor agent retrieves the number of the placed object and the total weight from the devices. All retrieved data is directly forwarded to the database.
Apart from a direct communication path from device to the agent, this sensor can also connect through a runtime environment (RE), whose implementation is available.
 
MQTT broker, Database and UUID are defined in the `resources/config/entity.xml` file.


## Usage
1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  

## Contacts

The following persons can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)
- Orhan Can Görür