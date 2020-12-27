package io.quanserds.comm;

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
    private byte[] receivePacketBuffer = new byte[0];
    private int receivePacketSize = 0;
    private int receivePacketContainerIndex = 0;

    private final Stream serverStream;

    public ModularServer(int port) {
        serverStream = new Stream(port, readBuffer);

        System.out.println("Waiting for simulation to connect...");

        while (true) {
            if (serverStream.poll(3)) {
                break;
            }
        }

        System.out.println("Connection accepted");
        System.out.println("Simulation connected");
    }

    public static byte[] join(byte[] a, byte[] b, int bLength) {
        byte[] joined = new byte[a.length + bLength];
        System.arraycopy(a, 0, joined, 0, a.length);
        System.arraycopy(b, 0, joined, a.length, bLength);
        return joined;
    }

    public void sendContainer(ModularContainer container) {
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

        serverStream.send(bb);
    }

    public void queueContainer(ModularContainer container) {
        ByteBuffer bb = ByteBuffer.allocate(10 + container.getPayload().length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(container.getContainerSize());
        bb.putInt(container.getDeviceID());
        bb.put((byte) container.getDeviceNumber());
        bb.put((byte) container.getDeviceFunction());
        bb.put(container.getPayload());

        byte[] bytes = bb.array();
        sendBuffer = join(sendBuffer, bytes, bytes.length);
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

        serverStream.send(bb);
        sendBuffer = new byte[0];
    }

    /**
     * Check if new data is available.
     * @return  true if a complete packet has been received.
     */
    public boolean receiveNewData() {
        int bytesRead = serverStream.receive();
        boolean newData = false;

        while (bytesRead > 0) {
            receivePacketBuffer = join(receivePacketBuffer, readBuffer, bytesRead);
            // while we're here, check if there are any more bytes in the receive buffer
            bytesRead = serverStream.receive();
        }

        // check if we already have data in the receive buffer that was unprocessed
        // (multiple packets in a single receive)
        if (receivePacketBuffer.length > 5) {
            if (receivePacketBuffer[4] == MAGIC_BYTE) {
                // packet size
                ByteBuffer bb = ByteBuffer.wrap(receivePacketBuffer);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                // add the 4 bytes for the size to the packet size
                receivePacketSize = bb.getInt() + 4;

                if (receivePacketBuffer.length >= receivePacketSize) {
                    receivePacketContainerIndex = 5;
                    newData = true;
                }
            } else {
                System.out.println("Error parsing multiple packets in receive buffer. " +
                        " Clearing internal buffers.");
                receivePacketBuffer = new byte[0];
            }
        }
        return newData;
    }

    public List<ModularContainer> getReceivedContainers() {
        if (receivePacketContainerIndex <= 0) {
            return Collections.emptyList();
        }
        List<ModularContainer> containers = new ArrayList<>();
        ByteBuffer bb = ByteBuffer.wrap(receivePacketBuffer);
        bb.position(receivePacketContainerIndex);
        bb.order(ByteOrder.BIG_ENDIAN);

        while (receivePacketContainerIndex < receivePacketSize) {
            int containerSize = bb.getInt(); // Originally: unsigned int
            int deviceID = bb.getInt(); // Originally: unsigned int
            int deviceNumber = bb.get();
            int deviceFunction = bb.get();
            byte[] payload = new byte[containerSize - 10];
            bb.get(payload);

            var container = ModularContainer.
                    of(containerSize, deviceID, deviceNumber,deviceFunction,payload);

            containers.add(container);
            receivePacketContainerIndex += containerSize;
        }

        if (receivePacketBuffer.length == receivePacketSize) {
            // The data buffer contains only the one packet.  Clear the buffer.
            receivePacketBuffer = new byte[0];
        } else {
            // Remove the packet from the data buffer.
            // There is another packet in the buffer already.
            receivePacketBuffer = Arrays.copyOfRange(receivePacketBuffer,
                    receivePacketContainerIndex, receivePacketBuffer.length);
        }

        receivePacketContainerIndex = 0;

        return containers;
    }

    @Override
    public void close() throws Exception {
        serverStream.close();
        System.out.println("Comm Server Closed");
    }
}
