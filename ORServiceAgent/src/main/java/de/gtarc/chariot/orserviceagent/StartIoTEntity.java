package de.gtarc.chariot.orserviceagent;

import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import org.springframework.context.ApplicationContext;

public class StartIoTEntity {

    public static void main(String[] args) {

        ApplicationContext context = SimpleAgentNode.startAgentNode("classpath:config/entity.xml", "jiactng_log4j.properties");
        SimpleAgentNode node = (SimpleAgentNode) context.getBean("ServiceNode");
        try {
            System.out.println("Start Node");
            node.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
