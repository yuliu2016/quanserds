package io.quanserds.comm;

import io.quanserds.comm.api.Container;
import io.quanserds.comm.api.Postman;

import static io.quanserds.comm.api.CommAPI.*;

public class EMG {
    private final Postman comms;
    private final int device_num;
    private double emg_left;
    private double emg_right;

    public EMG(Postman comms, int device_num) {
        this.comms = comms;
        this.device_num = device_num;
        System.out.println("Virtual EMG initialized");
    }

    private void readAllSensors() {
        comms.postMail(EMG_RequestState(device_num)).deliver().sleep(0.1);

        var containers = comms.checkMail(ID_EMG_INTERFACE);

        while (containers.isEmpty()) {
            comms.sleep(0.01);
            containers =  comms.checkMail(ID_EMG_INTERFACE);
        }

        for (Container container : containers) {
            if (container.getDeviceFunction() == FCN_EMG_RESPONSE_STATE) {
                var resp = EMG_ResponseState(container);
                emg_left = resp[0];
                emg_right = resp[1];
            }
        }
    }

    public double getEmgLeft() {
        return emg_left;
    }

    public double getEmgRight() {
        return emg_right;
    }

    public void ping() {
        comms.postMail(common_RequestPing(ID_EMG_INTERFACE, 0)).deliver();
    }
}
