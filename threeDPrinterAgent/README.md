# 3D Printer Actuator Agent

This agent contains the most important functions that enable to interact with 3D Printer controlled by [OctoPrint](https://octoprint.org/) 
3D Printer Agent embeds an OctoPrint API at [https://robweber.github.io/octoprint-java-lib/](https://robweber.github.io/octoprint-java-lib/), which is a simple wrapper to provide Java-bad communication to an OcroPrint Server using the [REST API](http://docs.octoprint.org/en/master/api/)
This wrapper is partially extended not only to broaden  its function set but also to satisfy the requirement of the CHARIOT demonstrator environment.
 
OctoPrint API establishes the communication using HTTP protocol with OctoPrint server and receives all commands. 
The status information requested by this agent are obtained with the same way. 

The available functions in 3D Printer agent can be extended according to the requirements as long as OctoPrint Server supports. For the sake of the simplicity,
only the most required functions are called in the agent implementation.
 
## Usage
1. compile the code, `mvn clean install`
2. start the agent, `./startAgent` or `./target/appassembler/bin/startIoTEntity`
3. stop the agent, `./stopAgent`  

## Contacts

The following person can answer your questions: 

- Cem Akpolat: [akpolatcem@gmail.com](mailto://akpolatcem@gmail.com)