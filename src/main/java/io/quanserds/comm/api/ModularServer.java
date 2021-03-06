package io.quanserds.comm.api;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModularServer implements AutoCloseable {
    private static final int BUFFER_SIZE = 65537;
    private static final byte MAGIC_BYTE = 123;

    private final byte[] readBuffer = new byte[BUFFER_SIZE];
    private byte[] sendBuffer = new byte[0];

    // Packet data
    private byte[] packetBuffer = new byte[0];
    private int packetSize = 0;
    private int packetIndex = 0;

    private final Stream serverStream;

    public ModularServer(int port) {
        serverStream = new Stream(port, readBuffer);
    }

    public void connect(boolean synchronousAccept) {
        serverStream.connect();

        if (!synchronousAccept) return;

        System.out.println("Waiting for simulation to connect...");

        while (true) {
            if (serverStream.acceptWithTimeout(3)) {
                break;
            }
        }

        System.out.println("Connection accepted");
        System.out.println("Simulation connected");
    }

    public boolean acceptAsynchronously() {
        return serverStream.acceptAsynchronously();
    }

    public void disconnect() {
        serverStream.disconnect(false);
        System.out.println("Disconnected From Client");
    }

    public String getClientAddress() {
        return serverStream.getClientAddress();
    }

    public void sendContainer(Container container) {
        ByteBuffer bb = ByteBuffer.allocate(15 + container.getPayload().length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(1 + container.getContainerSize());
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(MAGIC_BYTE);
        bb.putInt(container.getContainerSize());
        bb.putInt(container.getDeviceID());
        bb.put((byte) container.getDeviceNumber());
        bb.put((byte) container.getDeviceFunction());
        bb.put(container.getPayload());

        bb.flip(); // Prepare for writing the buffer
        serverStream.send(bb);
    }

    public void queueContainer(Container container) {
        ByteBuffer bb = ByteBuffer.allocate(10 + container.getPayload().length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(container.getContainerSize());
        bb.putInt(container.getDeviceID());
        bb.put((byte) container.getDeviceNumber());
        bb.put((byte) container.getDeviceFunction());
        bb.put(container.getPayload());

        byte[] bytes = bb.array();
        sendBuffer = CommUtil.join(sendBuffer, bytes, bytes.length);
    }

    public void sendQueue() {
        if (sendBuffer.length == 0) {
            return;
        }
        ByteBuffer bb = ByteBuffer.allocate(5 + sendBuffer.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(1 + sendBuffer.length);
        bb.put(MAGIC_BYTE);
        bb.put(sendBuffer);

        bb.flip(); // Prepare for writing the buffer
        serverStream.send(bb);
        sendBuffer = new byte[0];
    }

    /**
     * Check if new data is available.
     *
     * @return true if a complete packet has been received.
     */
    public boolean receiveNewData() {
        int bytesRead = serverStream.receive();
        boolean newData = false;

        while (bytesRead > 0) {
            packetBuffer = CommUtil.join(packetBuffer, readBuffer, bytesRead);
            // while we're here, check if there are any more bytes in the receive buffer
            bytesRead = serverStream.receive();
        }

        // check if we already have data in the receive buffer that was unprocessed
        // (multiple packets in a single receive)
        if (packetBuffer.length > 5) {
            if (packetBuffer[4] == MAGIC_BYTE) {
                // packet size
                ByteBuffer bb = ByteBuffer.wrap(packetBuffer);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                // add the 4 bytes for the size to the packet size
                packetSize = bb.getInt() + 4;

                if (packetBuffer.length >= packetSize) {
                    packetIndex = 5;
                    newData = true;
                }
            } else {
                System.out.println("Error parsing multiple packets in receive buffer. " +
                        " Clearing internal buffers.");
                packetBuffer = new byte[0];
            }
        }
        return newData;
    }

    public List<Container> getReceivedContainers() {
        if (packetIndex <= 0) {
            return Collections.emptyList();
        }
        List<Container> containers = new ArrayList<>();
        ByteBuffer bb = ByteBuffer.wrap(packetBuffer);
        bb.position(packetIndex);
        bb.order(ByteOrder.BIG_ENDIAN);

        while (packetIndex < packetSize) {
            int containerSize = bb.getInt(); // Originally: unsigned int
            int deviceID = bb.getInt(); // Originally: unsigned int
            int deviceNumber = bb.get();
            int deviceFunction = bb.get();
            byte[] payload = new byte[containerSize - 10];
            bb.get(payload);

            var container = Container.
                    of(containerSize, deviceID, deviceNumber, deviceFunction, payload);

            containers.add(container);
            packetIndex += containerSize;
        }

        if (packetBuffer.length == packetSize) {
            // The data buffer contains only the one packet.  Clear the buffer.
            packetBuffer = new byte[0];
        } else {
            // Remove the packet from the data buffer.
            // There is another packet in the buffer already.
            packetBuffer = Arrays.copyOfRange(packetBuffer,
                    packetIndex, packetBuffer.length);
        }

        packetIndex = 0;

        return containers;
    }

    @Override
    public void close() {
        serverStream.close();
        System.out.println("Comm Server Closed");
    }
}
