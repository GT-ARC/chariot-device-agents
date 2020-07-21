# Color Sensor Agent
The conveyor belt is equipped with a color sensor that detects tree colors red, blue and green.
The color of the passing objects on the conveyor belt can be figured out. 
The integration of this sensor is performed not only via agent but also via CHARIOT RE.

If an agent is used for the communication, then a web socket communication is built between the agent and device.
If RE takes the bridge role between agent and device, then the communication is based on MQTT protocol.

The execution of this agent considering of the aforementioned methods should be easily performed
` mvn clean install` command, after adjusting `resources/config/entity.xml` file, in which the path of the agent is given.

## usage
1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  

## Contacts

The following persons can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)
- Orhan Can Görür