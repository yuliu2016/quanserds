package io.quanserds.comm.api;

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

    private AsynchronousServerSocketChannel serverChannel;
    private Future<AsynchronousSocketChannel> acceptFuture;

    private AsynchronousSocketChannel clientChannel;
    private Future<Integer> readFuture = null;

    private final ByteBuffer readBuffer;

    public Stream(int port, byte[] buffer) {
        try {
            var localhost = InetAddress.getByName("localhost");
            serverChannel = AsynchronousServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(localhost, port));
        } catch (IOException e) {
            e.printStackTrace();
        }

        acceptFuture = serverChannel.accept();
        readBuffer = ByteBuffer.wrap(buffer);
    }

    public boolean poll(int timeoutSeconds) {
        if (clientChannel != null) {
            return false;
        }
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

    public void stop() {
        try {
            if (clientChannel != null && clientChannel.isOpen()) {
                clientChannel.close();
                clientChannel = null;
            }
            serverChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reconnect() {
        if (clientChannel != null && clientChannel.isOpen()) {
            try {
                clientChannel.close();
                clientChannel = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        acceptFuture = serverChannel.accept();
    }

    public void send(ByteBuffer buffer) {
        // Doesn't actually check if it worked or not
        clientChannel.write(buffer);
    }

    public void send(byte[] data) {
        send(ByteBuffer.wrap(data));
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
