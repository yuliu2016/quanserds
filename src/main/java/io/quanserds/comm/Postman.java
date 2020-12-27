package io.quanserds.comm;

import java.util.List;

public class Postman {

    private final ModularServer commServer;
    private List<ModularContainer> inbox;

    public Postman(int port) {
        System.out.println("Initializing Comm Server");
        commServer = new ModularServer(port);
        System.out.println("Comm Server Initialized");
    }

    public int fetch() {
        if (commServer.receiveNewData()) {
            List<ModularContainer> containers = commServer.getReceivedContainers();

        }
        return 0;
    }
}
