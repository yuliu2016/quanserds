package io.quanserds.comm.api;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CommUtil {
    static Container container(
            int device_id, int device_num, int device_func, byte[] payload) {
        return Container.of(10 + payload.length,
                device_id, device_num, device_func, payload);
    }

    static Container container(
            int device_id, int device_num, int device_func) {
        return container(device_id, device_num, device_func, new byte[0]);
    }

    static byte[] packFloat(float... floats) {
        ByteBuffer bb = ByteBuffer.allocate(floats.length * 4);
        bb.order(ByteOrder.BIG_ENDIAN);
        for (float aFloat : floats) {
            bb.putFloat(aFloat);
        }
        return bb.array();
    }

    static float unpackFloat(byte[] payload) {
        if (payload.length == 4) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            bb.order(ByteOrder.BIG_ENDIAN);
            return bb.getFloat();
        } else {
            return 0f;
        }
    }

    static float unpackFloat(Container container) {
        return unpackFloat(container.getPayload());
    }

    static int unpackInt(byte[] payload) {
        if (payload.length == 4) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            bb.order(ByteOrder.BIG_ENDIAN);
            return bb.getInt();
        } else {
            return 0;
        }
    }

    static int unpackInt(Container container) {
        return unpackInt(container.getPayload());
    }

    public static byte[] join(byte[] a, byte[] b, int bLength) {
        byte[] joined = new byte[a.length + bLength];
        System.arraycopy(a, 0, joined, 0, a.length);
        System.arraycopy(b, 0, joined, a.length, bLength);
        return joined;
    }
}
