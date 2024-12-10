package data;

import domain.SlaveCallback;

import java.io.IOException;
import java.net.*;

/**
 * The Slave class extends the Client class and handles sending messages to the master.
 */
public class Slave extends Client {
    private final SlaveCallback slaveCallback;

    /**
     * Constructs a Slave object.
     *
     * @param slaveCallback the callback interface for slave messages
     * @param port          the port number to bind the socket
     * @param number        the number to send to the master
     */
    public Slave(SlaveCallback slaveCallback, int port, int number) {
        super(port, number);
        this.slaveCallback = slaveCallback;

        run();
    }

    /**
     * Runs the main loop to send messages to the master.
     */
    private void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = String.valueOf(getNumber());
            InetAddress localHost = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), localHost, getPort());
            socket.send(packet);
        } catch (SocketException | UnknownHostException e) {
            slaveCallback.slveErrorMessage("Cannot find master");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}