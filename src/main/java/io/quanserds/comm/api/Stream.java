package io.quanserds.comm.api;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * equivalent to quanser.communications.Stream
 */
public class Stream implements AutoCloseable {

    // https://www.baeldung.com/java-nio2-async-socket-channel

    private final AsynchronousServerSocketChannel serverChannel;

    private Future<AsynchronousSocketChannel> acceptFuture;

    private AsynchronousSocketChannel clientChannel;

    private Future<Integer> readFuture = null;
    private final ByteBuffer readBuffer;
    private final byte[] inputArray;
    private final byte[] outputArray;

    private static AsynchronousServerSocketChannel open(int port) {
        try {
            var localhost = InetAddress.getByName("localhost");
            var sc = AsynchronousServerSocketChannel.open();
            sc.bind(new InetSocketAddress(localhost, port));
            return sc;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream(int port, byte[] outputReadBuffer) {
        if (port < 1000 || outputReadBuffer == null || outputReadBuffer.length < 256) {
            throw new IllegalArgumentException();
        }

        serverChannel = open(port);
        outputArray = outputReadBuffer;
        inputArray = new byte[outputReadBuffer.length];
        readBuffer = ByteBuffer.wrap(inputArray);
    }

    public void connect() {
        if (!serverChannel.isOpen()) {
            throw new IllegalStateException("Server Channel has already closed");
        }
        if (clientChannel != null) {
            throw new IllegalStateException("There is already a client; Disconnect First!");
        }
        if (acceptFuture != null) {
            throw new IllegalStateException("Already trying to connect");
        }
        acceptFuture = serverChannel.accept();
    }

    public void disconnect(boolean stopServer) {
        try {
            if (clientChannel != null && clientChannel.isOpen()) {
                clientChannel.close();
                clientChannel = null;
            }

            // ensure that there can be a reconnection later
            acceptFuture = null;

            if (stopServer) {
                serverChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientAddress() {
        if (clientChannel == null || !clientChannel.isOpen()) {
            throw new IllegalStateException("No client to get address from");
        }
        String addr = "N/A";
        try {
            SocketAddress sa = clientChannel.getRemoteAddress();
            if (sa instanceof InetSocketAddress) {
                var inet = (InetSocketAddress) sa;
                addr = inet.getHostString() + ":" + inet.getPort();
            }
        } catch (IOException e) {
            addr = "I/O Error";
            e.printStackTrace();
        }

        return addr;
    }

    public boolean acceptAsynchronously() {
        return acceptWithTimeout(-1);
    }

    public boolean acceptWithTimeout(int timeoutSeconds) {
        if (clientChannel != null) {
            throw new IllegalStateException("Cannot wait to accept when there's already a client");
        }

        if (acceptFuture == null) {
            throw new IllegalStateException("Nothing to accept in the future");
        }

        try {
            if (timeoutSeconds > 0) {
                // Synchronous: Wait for a certain number of seconds before returning
                clientChannel = acceptFuture.get(timeoutSeconds, TimeUnit.SECONDS);
            } else {
                // Asynchronous: Returns immediately if it's done
                if (acceptFuture.isDone()) {
                    clientChannel = acceptFuture.get();
                } else {
                    return false;
                }
            }

            boolean accepted = clientChannel != null && clientChannel.isOpen();
            if (accepted) {
                prepareNextRead();
            }
            return accepted;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // Didn't get anything due to timeout, need to call this function again to try
            return false;
        }
    }

    public void send(ByteBuffer buffer) {
        if (clientChannel == null) {
            throw new IllegalStateException("No client present to send data to");
        }

        if (outboundCompletionHandler == null) {
            clientChannel.write(buffer);
        } else {
            clientChannel.write(buffer, CommUtil.bytesToHex(buffer.array()),
                    outboundCompletionHandler);
        }
    }

    private void prepareNextRead() {
        // Clear the buffer to prepare for reading
        readBuffer.clear();
        readFuture = clientChannel.read(readBuffer);
    }

    public int receive() {
        if (readFuture == null) {
            throw new IllegalStateException("No data read future to receive from");
        }

        if (readFuture.isDone()) {
            int bytesRead = 0;
            try {
                bytesRead = readFuture.get();
            } catch (InterruptedException | ExecutionException ignored) {
                // No bytes are read
            }

            readFuture = null;

            // Copy over the data; make sure nothing is reading right now
            System.arraycopy(inputArray, 0, outputArray, 0, bytesRead);

            prepareNextRead();

            return bytesRead;
        } else {
            // It hasn't finished reading the buffer yet...
            return 0;
        }
    }

    @Override
    public void close() {
        disconnect(true);
    }

    private static CompletionHandler<Integer, String> outboundCompletionHandler = null;

    public static void setOutboundDataListener(BiConsumer<Boolean, String> outboundDataListener) {
        if (outboundDataListener == null) {
            throw new IllegalArgumentException();
        }
        outboundCompletionHandler = new CompletionHandler<>() {
            @Override
            public void completed(Integer result, String attachment) {
                if (attachment != null) {
                    outboundDataListener.accept(true, attachment);
                }
            }

            @Override
            public void failed(Throwable exc, String attachment) {
                if (attachment != null) {
                    outboundDataListener.accept(false, attachment);
                }
            }
        };
    }
}
