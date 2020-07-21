package de.gtarc.chariot.conveyorbeltactuatoragent;

import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

import org.springframework.context.ApplicationContext;

public class StartIoTEntity {

    public static void main(String[] args) throws LifecycleException {
        System.setProperty("log4j.configuration", "log4j.properties");
        // start node
        ApplicationContext context = SimpleAgentNode.startAgentNode("classpath:config/entity.xml", "log4j.properties");
        SimpleAgentNode node = (SimpleAgentNode) context.getBean("ActuatorNode");
        try {
            node.start();
        } catch (LifecycleException e) {	
            e.printStackTrace();
        }
    }
}
