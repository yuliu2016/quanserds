package io.quanserds.comm.api;

import java.util.ArrayList;
import java.util.List;

import static io.quanserds.comm.api.CommAPI.*;

public class Postman implements AutoCloseable {

    private final ModularServer commServer;
    private List<Container> inbox_OTHER = new ArrayList<>();
    private List<Container> inbox_EMG = new ArrayList<>();
    private List<Container> inbox_QBOT = new ArrayList<>();
    private List<Container> inbox_QARM = new ArrayList<>();
    private List<Container> inbox_TABLE = new ArrayList<>();
    private List<Container> inbox_GENERIC_SPAWNER = new ArrayList<>();
    private List<Container> inbox_AUTOCLAVE = new ArrayList<>();

    public Postman(int port) {
        System.out.println("Initializing Comm Server");
        commServer = new ModularServer(port);
        System.out.println("Comm Server Initialized");
    }

    public int fetch() {
        if (commServer.receiveNewData()) {
            List<Container> containers = commServer.getReceivedContainers();
            for (Container container : containers) {
                int id = container.getDeviceID();
                if (id == ID_QBOT || id == ID_QBOT_BOX) {
                    inbox_QBOT.add(container);
                } else if (id == ID_QARM) {
                    inbox_QARM.add(container);
                } else if (id == ID_SRV02BOTTLETABLE) {
                    inbox_TABLE.add(container);
                } else if (id == ID_EMG_INTERFACE) {
                    inbox_EMG.add(container);
                } else if (id == ID_GENERIC_SPAWNER) {
                    inbox_GENERIC_SPAWNER.add(container);
                } else if (id == ID_AUTOCLAVE) {
                    inbox_AUTOCLAVE.add(container);
                } else {
                    inbox_OTHER.add(container);
                    if (inbox_OTHER.size() > 10) {
                        inbox_OTHER.remove(0);
                    }
                }
            }
            return containers.size();
        }
        return 0;
    }

    public List<Container> checkMail(int deviceID, int deviceNum) {
        List<Container> out;

        if (deviceID == ID_QBOT) {
            out = inbox_QBOT;
            inbox_QBOT = new ArrayList<>();
        } else if (deviceID == ID_QARM) {
            out = inbox_QARM;
            inbox_QARM = new ArrayList<>();
        } else if (deviceID == ID_SRV02BOTTLETABLE) {
            out = inbox_TABLE;
            inbox_TABLE = new ArrayList<>();
        } else if (deviceID == ID_EMG_INTERFACE) {
            out = inbox_EMG;
            inbox_EMG = new ArrayList<>();
        } else if (deviceID == ID_GENERIC_SPAWNER) {
            out = inbox_GENERIC_SPAWNER;
            inbox_GENERIC_SPAWNER = new ArrayList<>();
        } else if (deviceID == ID_AUTOCLAVE) {
            out = inbox_AUTOCLAVE;
            inbox_AUTOCLAVE = new ArrayList<>();
        } else if (deviceID > 0) {
            out = new ArrayList<>();
            List<Container> noMatch = new ArrayList<>();
            for (Container c : inbox_OTHER) {
                if (c.getDeviceID() == deviceID && c.getDeviceNumber() == deviceNum) {
                    out.add(c);
                } else {
                    noMatch.add(c);
                }
            }
            inbox_OTHER = noMatch;
        } else {
            out = inbox_OTHER;
            inbox_OTHER = new ArrayList<>();
        }

        return out;
    }

    public void postMail(Container post) {
        commServer.queueContainer(post);
    }

    public void expressMail(Container post) {
        commServer.sendContainer(post);
    }

    public void deliver() {
        commServer.sendQueue();
    }

    public void flush() {
        deliver();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fetch();
        inbox_OTHER = new ArrayList<>();
        inbox_EMG = new ArrayList<>();
        inbox_QBOT = new ArrayList<>();
        inbox_QARM = new ArrayList<>();
        inbox_TABLE = new ArrayList<>();
        inbox_GENERIC_SPAWNER = new ArrayList<>();
        inbox_AUTOCLAVE = new ArrayList<>();
    }

    @Override
    public void close() throws Exception {
        commServer.sendQueue();
        commServer.close();
    }
}
