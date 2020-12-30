package io.quanserds.comm;

import io.quanserds.comm.api.Container;
import io.quanserds.comm.api.Postman;
import io.quanserds.comm.struct.ProximityData;

import static io.quanserds.comm.api.CommAPI.*;

public class SRV02 {
    private final Postman comms;
    private final int device_num;

    private int encoder_value;
    private float tof_value;
    private float rel_x;
    private float rel_y;
    private float rel_z;
    private String properties;
    private ProximityData proximityData;

    public SRV02(Postman comms, int device_num) {
        this.comms = comms;
        this.device_num = device_num;
        System.out.println("Virtual rotary table initialized");
    }

    public void readAllSensors() {
        comms
                .postMail(srv02BottleTable_RequestEncoder(0))
                .postMail(srv02BottleTable_RequestTOF(0))
                .postMail(srv02BottleTable_RequestProximityShort(0))
                .deliver()
                .sleep(0.1);

        int count = 0;
        while (count == 0) {
            count = comms.fetch();
            comms.sleep(0.01);
        }

        var containers = comms.checkMail(ID_SRV02BOTTLETABLE);

        // Parse each container
        for (Container container : containers) {
            int func = container.getDeviceFunction();
            if (func == FCN_SRV02BT_RESPONSE_ENCODER) {
                encoder_value = srv02BottleTable_ResponseEncoder(container);
            } else if (func == FCN_SRV02BT_RESPONSE_TOF) {
                tof_value = srv02BottleTable_ResponseTOF(container);
            } else if (func == FCN_SRV02BT_RESPONSE_PROXIMITY_SHORT) {
                var data = srv02BottleTable_ResponseProximityShort(container);
                proximityData = data;
                rel_x = data.relative_x;
                rel_y = data.relative_y;
                rel_z = data.relative_z;
                properties = data.properties;
            }
        }
    }

    public int readEncoder() {
        readAllSensors();
        return encoder_value;
    }

    public int readTOFSensor() {
        readAllSensors();
        return (int) tof_value;
    }

    public ProximityData readProximitySensor() {
        readAllSensors();
        return proximityData;
    }

    private void rotate(double speed) {
        comms.postMail(srv02BottleTable_CommandSpeed(0, (float) speed))
                .deliver()
                .sleep(0.1);
    }

    public void rotateClockwise(double speed) {
        rotate(speed);
    }

    public void rotateCounterclockwise(double speed) {
        rotate(-speed);
    }
}
