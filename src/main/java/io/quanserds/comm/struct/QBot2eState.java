package io.quanserds.comm.struct;

import java.nio.ByteBuffer;

public class QBot2eState {
    public final float world_x;
    public final float world_y;
    public final float world_z;

    public final float forward_x;
    public final float forward_y;
    public final float forward_z;

    public final float up_x;
    public final float up_y;
    public final float up_z;

    public final int bumper_front;
    public final int bumper_left;
    public final int bumper_right;

    public final float gyro;
    public final float heading;

    public final int encoder_left;
    public final int encoder_right;

    private QBot2eState(float world_x,
                        float world_y,
                        float world_z,
                        float forward_x,
                        float forward_y,
                        float forward_z,
                        float up_x,
                        float up_y,
                        float up_z,
                        int bumper_front,
                        int bumper_left,
                        int bumper_right,
                        float gyro,
                        float heading,
                        int encoder_left,
                        int encoder_right) {
        this.world_x = world_x;
        this.world_y = world_y;
        this.world_z = world_z;
        this.forward_x = forward_x;
        this.forward_y = forward_y;
        this.forward_z = forward_z;
        this.up_x = up_x;
        this.up_y = up_y;
        this.up_z = up_z;
        this.bumper_front = bumper_front;
        this.bumper_left = bumper_left;
        this.bumper_right = bumper_right;
        this.gyro = gyro;
        this.heading = heading;
        this.encoder_left = encoder_left;
        this.encoder_right = encoder_right;
    }

    public static QBot2eState fromPayload(byte[] payload) {
        if (payload.length == 55) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            return new QBot2eState(
                    bb.getFloat(), bb.getFloat(), bb.getFloat(),
                    bb.getFloat(), bb.getFloat(), bb.getFloat(),
                    bb.getFloat(), bb.getFloat(), bb.getFloat(),
                    bb.get(), bb.get(), bb.get(),
                    bb.getFloat(), bb.getFloat(),
                    bb.getInt(), bb.getInt()
            );
        } else {
            return new QBot2eState(
                    0f, 0f, 0f,
                    0f, 0f, 0f,
                    0f, 0f, 0f,
                    0, 0, 0,
                    0f, 0f,
                    0, 0);
        }
    }
}
