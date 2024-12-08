package data;

import domain.SlaveCallback;

import java.io.IOException;
import java.net.*;

public class Slave extends Client{
private final SlaveCallback slaveCallback;
    public Slave(SlaveCallback slaveCallback, int port, int number) {
        super(port, number);
        this.slaveCallback = slaveCallback;

        run();
    }

    private void run(){
        try {
            DatagramSocket socket = new DatagramSocket();
            String message = String.valueOf(getNumber());
            InetAddress localHost = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), localHost, getPort());
            socket.send(packet);
        }catch (SocketException | UnknownHostException e){
            slaveCallback.slveErrorMessage("Cannot find master");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
