package io.quanserds.comm.struct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class QArmState {
    public final float base;
    public final float shoulder;
    public final float elbow;
    public final float wrist;
    public final float gripper;
    public final int static_environment_collision;
    public final int finger_pad_detection_right_proximal;
    public final int finger_pad_detection_right_distal;
    public final int finger_pad_detection_left_proximal;
    public final int finger_pad_detection_left_distal;

    private QArmState(
            float base,
            float shoulder,
            float elbow,
            float wrist,
            float gripper,
            int static_environment_collision,
            int finger_pad_detection_right_proximal,
            int finger_pad_detection_right_distal,
            int finger_pad_detection_left_proximal,
            int finger_pad_detection_left_distal) {
        this.base = base;
        this.shoulder = shoulder;
        this.elbow = elbow;
        this.wrist = wrist;
        this.gripper = gripper;
        this.static_environment_collision = static_environment_collision;
        this.finger_pad_detection_right_proximal = finger_pad_detection_right_proximal;
        this.finger_pad_detection_right_distal = finger_pad_detection_right_distal;
        this.finger_pad_detection_left_proximal = finger_pad_detection_left_proximal;
        this.finger_pad_detection_left_distal = finger_pad_detection_left_distal;
    }

    public static QArmState fromPayload(byte[] payload) {
        if (payload.length == 25) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            bb.order(ByteOrder.BIG_ENDIAN);
            return new QArmState(bb.getFloat(), bb.getFloat(), bb.getFloat(),
                    bb.getFloat(), bb.getFloat(), bb.get(), bb.get(), bb.get(),
                    bb.get(), bb.get());
        } else {
            return new QArmState(0f,0f,0f,0f,0f,
                    0,
                    0,
                    0,
                    0,
                    0);
        }
    }
}
