package io.quanserds.comm;

import io.quanserds.comm.api.Container;
import io.quanserds.comm.api.Postman;
import io.quanserds.comm.struct.QArmGripperObject;
import io.quanserds.comm.struct.QArmGripperState;
import io.quanserds.comm.struct.QArmState;

import java.util.List;

import static io.quanserds.comm.api.CommAPI.*;

public class QArm {
    private final Postman comms;
    private final int device_num;

    public QArm(Postman comms, int device_num) {
        this.comms = comms;
        this.device_num = device_num;
        System.out.println("Virtual QArm initialized");
    }

    private float base;
    private float shoulder;
    private float elbow;
    private float wrist;
    private float gripper;
    private int static_environment_collision;
    private int finger_pad_detection_right_proximal;
    private int finger_pad_detection_right_distal;
    private int finger_pad_detection_left_proximal;
    private int finger_pad_detection_left_distal;

    private float base_r = 1f;
    private float base_g = 0f;
    private float base_b = 0f;
    private float arm_brightness = 1f;

    private int object_id = 0;
    private float object_mass = 0f;
    private String object_properties = "";

    private void updateArmState() {
        // Fetch until new data is received from simulation
        int count = 0;
        while (count == 0) {
            count = comms.fetch();
            comms.sleep(0.01);
        }

        List<Container> containers = comms.checkMail(ID_QARM);
        for (Container container : containers) {
            updateWithContainer(container);
        }
    }

    private void updateWithContainer(Container container) {
        switch (container.getDeviceFunction()) {
            case FCN_QARM_RESPONSE_STATE:
                QArmState s = qarm_ResponseState(container);
                base = s.base;
                shoulder = s.shoulder;
                elbow = s.elbow;
                wrist = s.wrist;
                gripper = s.gripper;
                static_environment_collision = s.static_environment_collision;
                finger_pad_detection_right_proximal = s.finger_pad_detection_right_proximal;
                finger_pad_detection_right_distal = s.finger_pad_detection_right_distal;
                finger_pad_detection_left_proximal = s.finger_pad_detection_left_proximal;
                finger_pad_detection_left_distal = s.finger_pad_detection_left_distal;
                break;

            case FCN_QARM_RESPONSE_BASE:
                base = qarm_ResponseBase(container);
                break;

            case FCN_QARM_RESPONSE_SHOULDER:
                shoulder = qarm_ResponseShoulder(container);
                break;

            case FCN_QARM_RESPONSE_ELBOW:
                elbow = qarm_ResponseElbow(container);
                break;

            case FCN_QARM_RESPONSE_WRIST:
                wrist = qarm_ResponseWrist(container);
                break;

            case FCN_QARM_RESPONSE_GRIPPER:
                QArmGripperState g = qarm_ResponseGripper(container);
                gripper = g.gripper;
                static_environment_collision = g.static_environment_collision;
                finger_pad_detection_right_proximal = g.finger_pad_detection_right_proximal;
                finger_pad_detection_right_distal = g.finger_pad_detection_right_distal;
                finger_pad_detection_left_proximal = g.finger_pad_detection_left_proximal;
                finger_pad_detection_left_distal = g.finger_pad_detection_left_distal;

            case FCN_QARM_RESPONSE_GRIPPER_OBJECT_PROPERTIES:
                QArmGripperObject p = qarm_ResponseGripperObjectProperties(container);
                object_id = p.object_id;
                object_mass = p.mass;
                object_properties = p.properties;
        }
    }

    public void readAllArmJoints() {
        updateArmState();
    }

    public void setBaseColor(double r, double g, double b) {
        base_r = (float) r;
        base_g = (float) g;
        base_b = (float) b;

        comms.postMail(qarm_CommandBaseColor(device_num, base_r, base_g, base_b))
                .deliver();
    }

    public void returnHome() {
        comms.postMail(qarm_CommandAndRequestState(0, 0, 0,
                0, 0, 0, base_r, base_g, base_b, 0))
                .deliver().sleep(0.1);
    }

    public void qarmMove(
            double base, double shoulder, double elbow, double wrist, double gripper) {
        comms.postMail(qarm_CommandAndRequestState(device_num, (float) base,
                (float) shoulder, (float) elbow,
                (float) wrist, (float) gripper,
                base_r, base_g, base_b, arm_brightness)).deliver();
    }

    public void qarmMoveBase(double base) {
        comms.postMail(qarm_CommandBase(device_num, (float) base)).deliver();
    }

    public void qarmMoveShoulder(double shoulder) {
        comms.postMail(qarm_CommandShoulder(device_num, (float) shoulder)).deliver();
    }

    public void qarmMoveElbow(double elbow) {
        comms.postMail(qarm_CommandElbow(device_num, (float) elbow)).deliver();
    }

    public void qarmMoveWrist(double wrist) {
        comms.postMail(qarm_CommandWrist(device_num, (float) wrist)).deliver();
    }

    public void qarmMoveGripper(double gripper) {
        comms.postMail(qarm_CommandGripper(device_num, (float) gripper)).deliver();
    }

    // Manipulator parameters in meters:
    private static final double _L1 = 0.127;
    private static final double _L2 = 0.3556;
    private static final double _L3 = 0.4064;

    // Define joint angle (in rads) and gripper limits
    private static final double _base_upper_lim = 3.05;
    private static final double _base_lower_lim = -3.05;
    private static final double _shoulder_upper_limit = 1.57;
    private static final double _shoulder_lower_limit = -1.57;
    private static final double _elbow_upper_limit = 1.57;
    private static final double _elbow_lower_limit = -1.39;
    private static final double _wrist_upper_limit = 2.96;
    private static final double _wrist_lower_limit = -2.96;
    private static final double _gripper_upper_limit = 1;
    private static final double _gripper_lower_limit = 0;

    public boolean anglesWithinBound(
            double base, double shoulder, double elbow, double wrist, double gripper) {
        // Check if given joint angles and gripper value are within acceptable limit
        return !(base > _base_upper_lim || base < _base_lower_lim ||
                shoulder > _shoulder_upper_limit || shoulder < _shoulder_lower_limit ||
                elbow > _elbow_upper_limit || elbow < _elbow_lower_limit ||
                wrist > _wrist_upper_limit || wrist < _wrist_lower_limit ||
                gripper > _gripper_upper_limit || gripper < _gripper_lower_limit);
    }

    public boolean coordinatesWithinBound(double x, double y, double z) {
        double R = Math.hypot(x, y);

        // Vertical offset within the verical plane from Frame 1 to End-Effector
        // Note: Frame 1 y-axis points downward (negative global Z-axis direction)
        double Z = _L1 - z;

        // Distance from Frame 1 to End-Effector Frame
        double Lambda = Math.hypot(R, Z);

        return !(Lambda > (_L2 + _L3) || z < 0);
    }
}
