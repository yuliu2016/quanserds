package io.quanserds.comm.api;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

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

    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
