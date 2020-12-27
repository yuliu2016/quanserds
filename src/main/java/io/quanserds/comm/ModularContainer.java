package io.quanserds.comm;

public class ModularContainer {
    private final int containerSize;
    private final int deviceID;
    private final int deviceNumber;
    private final int deviceFunction;
    private final byte[] payload;

    private ModularContainer(int containerSize,
                            int deviceID,
                            int deviceNumber,
                            int deviceFunction,
                            byte[] payload) {
        this.containerSize = containerSize;
        this.deviceID = deviceID;
        this.deviceNumber = deviceNumber;
        this.deviceFunction = deviceFunction;
        this.payload = payload;
    }

    public int getContainerSize() {
        return containerSize;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public int getDeviceFunction() {
        return deviceFunction;
    }

    public byte[] getPayload() {
        return payload;
    }

    public static ModularContainer of(int containerSize,
                             int deviceID,
                             int deviceNumber,
                             int deviceFunction,
                             byte[] payload) {
        return new ModularContainer(containerSize,
                deviceID, deviceNumber, deviceFunction, payload);
    }

    public static ModularContainer empty() {
        return of(0,0,0,0, new byte[0]);
    }
}
