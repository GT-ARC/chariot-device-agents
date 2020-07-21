package de.gtarc.chariot.distancemeasuringagent;


import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import org.springframework.context.ApplicationContext;

public class StartDeviceService {

    public static void main(String[] args) throws LifecycleException {
        ApplicationContext context = SimpleAgentNode.startAgentNode("classpath:config/entity.xml", "jiactng_log4j.properties");
        SimpleAgentNode node = (SimpleAgentNode) context.getBean("SensorNode");
        try {
            System.out.println("Start Node");
            node.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

}
