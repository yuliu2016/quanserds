package io.quanserds.comm.struct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WorldTransform {
    public final float pos_x;
    public final float pos_y;
    public final float pos_z;
    public final float rot_x;
    public final float rot_y;
    public final float rot_z;
    public final float scale_x;
    public final float scale_y;
    public final float scale_z;

    private WorldTransform(
            float pos_x,
            float pos_y,
            float pos_z,
            float rot_x,
            float rot_y,
            float rot_z,
            float scale_x,
            float scale_y,
            float scale_z
    ) {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.pos_z = pos_z;
        this.rot_x = rot_x;
        this.rot_y = rot_y;
        this.rot_z = rot_z;
        this.scale_x = scale_x;
        this.scale_y = scale_y;
        this.scale_z = scale_z;
    }

    public static WorldTransform fromPayload(byte[] payload) {
        if (payload.length == 36) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            bb.order(ByteOrder.BIG_ENDIAN);
            return new WorldTransform(
                    bb.getFloat(), bb.getFloat(), bb.getFloat(),
                    bb.getFloat(), bb.getFloat(), bb.getFloat(),
                    bb.getFloat(), bb.getFloat(), bb.getFloat()
            );
        } else {
            return new WorldTransform(
                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 0
            );
        }
    }
}
