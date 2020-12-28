package io.quanserds.comm;

import io.quanserds.comm.api.CommAPI;
import io.quanserds.comm.api.Container;
import io.quanserds.comm.api.Postman;

import java.util.List;

public class  Autoclave {
    private final int device_num;
    private final Postman comms;

    private boolean isOpen = false;

    public Autoclave(Postman comms, int device_num) {
        this.comms = comms;
        this.device_num = device_num;
    }

    public void openDrawer(boolean drawer) {
        isOpen = drawer;
        comms.postMail(CommAPI.autoclave_OpenDrawer(
                device_num, drawer ? 1 : 0)).deliver().sleep(0.01);
        waitForAck();
    }

    public void toggle() {
        openDrawer(!isOpen);
    }

    private void waitForAck() {
        List<Container> containers = comms.checkMail(CommAPI.ID_AUTOCLAVE);
        while (containers.isEmpty()) {
            int count = 0;
            while (count == 0) {
                count = comms.fetch();
                comms.sleep(0.01);
            }
            containers = comms.checkMail(CommAPI.ID_AUTOCLAVE);
        }
    }

    public void ping() {
        comms.postMail(CommAPI.common_RequestPing(0, 0));
        comms.deliver();
    }
}
