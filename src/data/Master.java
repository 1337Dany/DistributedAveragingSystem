package data;

import domain.MasterCallback;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * The Master class extends the Client class and handles receiving and broadcasting messages.
 */
public class Master extends Client {
    private final MasterCallback masterCallback;
    private DatagramSocket datagramSocket;
    private final List<Integer> numbers = Collections.synchronizedList(new ArrayList<>());
    private static final byte[] BYTE_BUFFER = new byte[1024];
    private boolean AVGRepeat = false;
    private int previousAVG;

    /**
     * Constructs a Master object.
     *
     * @param masterCallback the callback interface for master messages
     * @param port the port number to bind the socket
     * @param number the initial number to add to the list
     * @throws SocketException if there is an error creating the socket
     */
    public Master(MasterCallback masterCallback, int port, int number) throws SocketException {
        super(port, number);
        numbers.add(number);
        this.masterCallback = masterCallback;
        run();
    }

    /**
     * Runs the main loop to receive and process messages.
     *
     * @throws SocketException if there is an error creating the socket
     */
    private void run() throws SocketException {
        datagramSocket = new DatagramSocket(getPort());

        masterCallback.masterMessage("Master mode activated on port " + getPort());
        masterCallback.masterMessage("Initial number: " + getNumber());
        masterCallback.masterMessage("Waiting for connections...");

        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(BYTE_BUFFER, BYTE_BUFFER.length);
                datagramSocket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                int received = Integer.parseInt(message);

                if (AVGRepeat && received == previousAVG) {
                    AVGRepeat = false;
                    continue;
                }

                if (received == 0) {
                    int average = avg();
                    masterCallback.masterMessage("Broadcast average number: " + average);
                    broadcast(String.valueOf(average));
                } else if (received == -1) {
                    masterCallback.masterMessage("-1 received. Broadcast termination");
                    broadcast("-1");
                    break;
                } else {
                    masterCallback.masterMessage("Received number: " + received);
                    numbers.add(received);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the average of the numbers in the list.
     *
     * @return the average of the numbers
     */
    private int avg() {
        int sum = 0;
        for (int num : numbers) {
            if (num != 0) sum += num;
        }
        int result = (int) Math.floor((double) sum / numbers.size());
        AVGRepeat = true;
        previousAVG = result;
        return result;
    }

    /**
     * Broadcasts a message to the network.
     *
     * @param string the message to broadcast
     */
    private void broadcast(String string) {
        try {
            InetAddress address = getAddress(getMyIp(), getSubnetMask());
            datagramSocket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(string.getBytes(), string.length(), address, getPort());
            datagramSocket.send(packet);
            masterCallback.masterMessage("Average number transmitted to: " + address.getCanonicalHostName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the broadcast address based on the IP and subnet mask.
     *
     * @param ip the IP address
     * @param subnetMask the subnet mask
     * @return the broadcast address
     * @throws SocketException if there is an error with the socket
     * @throws UnknownHostException if the IP address is unknown
     */
    private InetAddress getAddress(String ip, int subnetMask) throws SocketException, UnknownHostException {
        byte[] ipBytes = InetAddress.getByName(ip).getAddress();
        int mask = -(1 << (32 - subnetMask));
        int ipAsInt = 0;
        for (byte b : ipBytes) {
            ipAsInt = (ipAsInt << 8) | (b & 0xFF);
        }
        int broadcastAsInt = ipAsInt | ~mask;
        byte[] broadcastBytes = new byte[4];
        for (int i = 3; i >= 0; i--) {
            broadcastBytes[i] = (byte) (broadcastAsInt & 0xFF);
            broadcastAsInt >>= 8;
        }
        return InetAddress.getByAddress(broadcastBytes);
    }

    /**
     * Gets the local IP address.
     *
     * @return the local IP address
     */
    private static String getMyIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Gets the subnet mask of the local network.
     *
     * @return the subnet mask
     */
    private int getSubnetMask() {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                if (address.getAddress() instanceof Inet4Address) {
                    return address.getNetworkPrefixLength();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}