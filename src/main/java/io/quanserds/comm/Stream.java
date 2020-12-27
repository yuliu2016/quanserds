package io.quanserds.comm;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.*;

/**
 * equivalent to quanser.communications.Stream
 */
public class Stream implements AutoCloseable {

    // https://www.baeldung.com/java-nio2-async-socket-channel

    private final AsynchronousServerSocketChannel serverChannel;
    private final Future<AsynchronousSocketChannel> acceptFuture;

    private AsynchronousSocketChannel clientChannel;
    private Future<Integer> readFuture = null;

    private final ByteBuffer readBuffer;

    public Stream(int port, byte[] buffer) throws IOException {
        var localhost = InetAddress.getByName("localhost");

        serverChannel = AsynchronousServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(localhost, port));

        acceptFuture = serverChannel.accept();

        readBuffer = ByteBuffer.wrap(buffer);
    }

    public boolean poll(int timeoutSeconds) {
        try {
            clientChannel = acceptFuture.get(timeoutSeconds, TimeUnit.SECONDS);
            boolean accepted = clientChannel != null && clientChannel.isOpen();
            if (accepted) {
                // Get ready to read the first data when receive is called
                readFuture = clientChannel.read(readBuffer);
            }
            return accepted;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return false;
        }
    }

    public void stop() throws IOException {
        if (clientChannel != null) {
            clientChannel.close();
        }
        serverChannel.close();
    }

    public void send(byte[] data) {
        // Doesn't actually check if it worked or not
        clientChannel.write(ByteBuffer.wrap(data));
    }

    public int receive() {
        if (readFuture.isDone()) {
            int bytesRead = 0;
            try {
                bytesRead = readFuture.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }

            readFuture = clientChannel.read(readBuffer);
            return bytesRead;
        } else {
            // It hasn't finished reading the buffer yet...
            return 0;
        }
    }

    @Override
    public void close() throws Exception {
        stop();
    }
}
