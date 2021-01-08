package io.quanserds.comm;

import io.quanserds.comm.api.Container;
import io.quanserds.comm.api.Postman;
import io.quanserds.comm.struct.QBot2eState;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.List;

import static io.quanserds.comm.api.CommAPI.*;

@SuppressWarnings("UnusedReturnValue")
public class QBot2e {

    private final Postman comms;
    private final int device_num;

    private boolean rgb_pending;
    private boolean depth_pending;
    private boolean cmd_pending;
    private boolean box_pending;

    private Image rgb;
    private Image depth;

    private QBot2eState state;

    public QBot2e(Postman comms, int device_num) {

        this.comms = comms;
        this.device_num = device_num;
        System.out.println("QBot2e Initialized");
    }

    private List<Container> checkMail() {
        comms.fetch();
        return comms.checkMail(ID_QBOT); // also includes ID_QBOT_BOX
    }

    private void refresh() {
        // ... sending some request for data
        comms.deliver();
        var inbox = checkMail();
        for (Container container : inbox) {
            parseContainer(container);
        }
    }

    private boolean requestRGB() {
        if (!rgb_pending) {
            comms.postMail(qbot2e_RequestRGB(device_num));
            refresh();
            rgb_pending = true;
            return true;
        }
        return false;
    }

    private boolean requestDepth() {
        if (!depth_pending) {
            comms.postMail(qbot2e_RequestDepth(device_num));
            refresh();
            depth_pending = true;
            return true;
        }
        return false;
    }

    private boolean command(double speed, double turn) {
        if (!cmd_pending) {
            comms.postMail(qbot2e_CommandAndRequestState(device_num,
                    (float) speed, (float) turn)).deliver();
            cmd_pending = true;
            return true;
        }
        return false;
    }

    private boolean boxCommand(
            double x, double y, double z, double pitch, double roll, double yaw) {
        if (!box_pending) {
            comms.postMail(qbot2eBox_Command(device_num,
                    (float) x, (float) y, (float) z,
                    (float) pitch, (float) roll, (float) yaw));
            box_pending = true;
            return true;
        }
        return false;
    }

    private void parseContainer(Container container) {
        var func = container.getDeviceFunction();
        if (func == FCN_QBOT_RESPONSE_STATE) {
            state = qbot2e_ResponseState(container);
            cmd_pending = false;
        } else if (func == FCN_QBOT_RESPONSE_RGB) {
            rgb = new Image(new ByteArrayInputStream(qbot2e_ResponseRGB(container)));
            rgb_pending = false;
        } else if (func == FCN_QBOT_RESPONSE_DEPTH) {
            depth = new Image(new ByteArrayInputStream(qbot2e_ResponseDepth(container)));
            depth_pending = false;
        } else if (func == FCN_QBOT_BOX_COMMAND_ACK) {
            box_pending = false;
        }
    }

    public QBot2eState getState() {
        return state;
    }

    public void ping() {
        comms.postMail(common_RequestPing(0, 0)).deliver();
    }

    private static final double QBOT_RADIUS = 0.235f / 2.0f;

    public void setVelocity(double L, double R) {
        double speed = (L + R) / 2;
        double turn = (R - L) / (2 * QBOT_RADIUS);
        double ref_count = 0;
        while (cmd_pending) {
            ref_count += 1;
            refresh();
            comms.sleep(0.01);
            if (ref_count > 10) {
                cmd_pending = false;
                break;
            }
        }
        command(speed, turn);
    }

    public void moveTime(double L, double R, double t_finish) {
        var t_start = System.nanoTime() / 1e9;
        var t_delta = 0.0;
        while (t_delta < t_finish) {
            setVelocity(L, R);
            var t_now = System.nanoTime() / 1e9;
            t_delta = t_now - t_start;
            comms.sleep(0.05);
        }
        halt();
    }

    public void halt() {
        cmd_pending = false;
        setVelocity(0, 0);
    }

    @SuppressWarnings("SameParameterValue")
    private void setBoxAttribute(
            double x, double y, double z, double pitch, double roll, double yaw) {
        int ref_count = 0;
        while (box_pending) {
            ref_count += 1;
            refresh();
            comms.sleep(0.01);
            if (ref_count > 10) {
                box_pending = false;
                break;
            }
        }
        boxCommand(x, y, z, pitch, roll, yaw);
    }

    private void setBoxAngle(double theta) {
        setBoxAttribute(0, 0.15 * (1 - Math.cos(theta)), 0.15 * Math.sin(theta),
                theta, 0, 0);
    }

    public void dump() {
        for (int i = 0; i < 100; i++) {
            var j = (i / 100.0) * (2 * Math.PI);
            setBoxAngle(1 - Math.cos(j));
        }
    }

    public Image getRGB() {
        refresh();
        requestRGB();
        return rgb;
    }

    public Image getNewRGB() {
        if (!rgb_pending) {
            requestRGB();
        }
        while (rgb_pending) {
            refresh();
        }
        return rgb;
    }

    public Image getDepth() {
        refresh();
        requestDepth();
        return depth;
    }

    public Image getNewDepth() {
        if (!depth_pending) {
            requestDepth();
        }
        while (depth_pending) {
            refresh();
        }
        return depth;
    }

    /**
     * Return single point at location (row, column) depth measurement in meters
     */
    public double measureDepth(int row, int col) {
        // # Get last depth frame
        var depth = getNewDepth().getPixelReader();
        //  # Extract central point; frame size 640 by 480; pixel values 0-255 (0~9.44 m)
        var d = depth.getColor(row, col).getGreen();
        //# Convert to m and return value
        return 9.44 * d / 255;
    }

    public double measureDepth() {
        return measureDepth(240, 320);
    }
}
