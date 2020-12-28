package io.quanserds.comm;

public class Container {
    private final int containerSize;
    private final int deviceID;
    private final int deviceNumber;
    private final int deviceFunction;
    private final byte[] payload;

    private Container(int containerSize,
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

    public static Container of(int containerSize,
                               int deviceID,
                               int deviceNumber,
                               int deviceFunction,
                               byte[] payload) {
        return new Container(containerSize,
                deviceID, deviceNumber, deviceFunction, payload);
    }
}
