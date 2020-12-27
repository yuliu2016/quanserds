package io.quanserds.comm.struct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class QArmGripperState {
    public final float gripper;
    public final int static_environment_collision;
    public final int finger_pad_detection_right_proximal;
    public final int finger_pad_detection_right_distal;
    public final int finger_pad_detection_left_proximal;
    public final int finger_pad_detection_left_distal;

    private QArmGripperState(
            float gripper,
            int static_environment_collision,
            int finger_pad_detection_right_proximal,
            int finger_pad_detection_right_distal,
            int finger_pad_detection_left_proximal,
            int finger_pad_detection_left_distal) {
        this.gripper = gripper;
        this.static_environment_collision = static_environment_collision;
        this.finger_pad_detection_right_proximal = finger_pad_detection_right_proximal;
        this.finger_pad_detection_right_distal = finger_pad_detection_right_distal;
        this.finger_pad_detection_left_proximal = finger_pad_detection_left_proximal;
        this.finger_pad_detection_left_distal = finger_pad_detection_left_distal;
    }

    public static QArmGripperState fromPayload(byte[] payload) {
        if (payload.length == 9) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            bb.order(ByteOrder.BIG_ENDIAN);
            return new QArmGripperState(bb.getFloat(), bb.get(), bb.get(), bb.get(),
                    bb.get(), bb.get());
        } else {
            return new QArmGripperState(0f,
                    0,
                    0,
                    0,
                    0,
                    0);
        }
    }
}
