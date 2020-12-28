package io.quanserds.comm.struct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ProximityData {
    public final float relative_x;
    public final float relative_y;
    public final float relative_z;
    public final String properties;

    private ProximityData(float relative_x,
                          float relative_y,
                          float relative_z,
                          String properties) {
        this.relative_x = relative_x;
        this.relative_y = relative_y;
        this.relative_z = relative_z;
        this.properties = properties;
    }

    public static ProximityData fromPayload(byte[] payload) {
        if (payload.length >= 16) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            bb.order(ByteOrder.BIG_ENDIAN);
            float x = bb.getFloat();
            float y = bb.getFloat();
            float z = bb.getFloat();

            int properties_size = bb.getInt();
            String properties;
            if (properties_size > 0) {
                byte[] bytes = new byte[properties_size];
                bb.get(bytes);
                properties = new String(bytes, StandardCharsets.UTF_8);
            } else {
                properties = "";
            }
            return new ProximityData(x, y, z, properties);
        } else {
            return new ProximityData(0, 0, 0, "");
        }
    }
}
