package io.quanserds.comm.struct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class QArmGripperObject {
    public final int object_id;
    public final float mass;
    public final String properties;

    private QArmGripperObject(int object_id, float mass, String properties) {
        this.object_id = object_id;
        this.mass = mass;
        this.properties = properties;
    }

    public static QArmGripperObject fromPayload(byte[] payload) {
        if (payload.length >= 9) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            bb.order(ByteOrder.BIG_ENDIAN);
            int object_id = bb.get();
            float mass = bb.getFloat();
            int properties_size = bb.getInt();
            String properties;
            if (properties_size > 0) {
                byte[] bytes = new byte[properties_size];
                bb.get(bytes);
                properties = new String(bytes, StandardCharsets.UTF_8);
            } else {
                properties = "";
            }
            return new QArmGripperObject(object_id, mass, properties);
        } else {
            return new QArmGripperObject(0, 0f, "");
        }
    }
}
