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

    // Encoder counts to degrees
    private static final double kEnc = 360.0 / 4096;
    private static final double kP = 0.02;
    private static final double kSaturationVoltage = 2.0;

    /**
     * Rotate table for given angle in degrees (closed-loop; proportional-only for now)
     * Both positive and negative angle can be commanded. However, do no rotate table
     * CCW past initial zero position. Encoder wraps.
     */
    public void commandPID(double angle) {
        int initial_encoder_count = readEncoder();

        double error = Math.abs(angle);
        double direction = Math.signum(angle);

        while (error > 0.05) {
            double speed = kP * error;
            if (speed > kSaturationVoltage) {
                speed = kSaturationVoltage;
            }
            rotate(direction * speed);

            int current_encoder_count = readEncoder();
            double current_angle;
            current_angle = (current_encoder_count - initial_encoder_count) * kEnc;
            if (angle > 0) {
                error = angle - current_angle;
            } else {
                error = current_angle - angle;
            }
        }

        stopTable();
    }

    public void stopTable() {
        rotate(0);
    }

    public void spawnSingleBottle(double r, double g, double b, double alpha, boolean metal) {
        comms.postMail(srv02BottleTable_SpawnContainer(device_num, 0.1f, 0.65f, metal ? 1 : 0,
                (float) r, (float) g, (float) b, (float) alpha, 1, 1,
                metal ? "metal" : "plastic")).deliver().sleep(0.1);
    }

    public void ping() {
        comms.postMail(common_RequestPing(ID_SRV02BOTTLETABLE, 0)).deliver();
    }
}
