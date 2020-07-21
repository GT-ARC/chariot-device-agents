# 3D Printer Service Agent

This service agent consumes the functions of the 3D Printer device agent and it plays a bridge role between an augmented reality application and 3D printer. 
AR application displays all features of the 3D printer on a smartphone screen and provide an interaction environment for the end user in order to control and monitor the 3D Printer.
The communication between AR smartphone app and this service agent is operated via MQTT protocol, on the other hand it realizes an agent-to-agent communication with 3D Printer device agent via ActiveMQ protocol.


## Usage
1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  

## Contacts

The following person can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)