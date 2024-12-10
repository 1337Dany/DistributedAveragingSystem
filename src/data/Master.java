package data;

import domain.MasterCallback;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Master extends Client {
    private final MasterCallback masterCallback;
    private DatagramSocket datagramSocket;
    private final List<Integer> numbers = Collections.synchronizedList(new ArrayList<>());
    private static final byte[] BYTE_BUFFER = new byte[1024];
    private boolean AVGRepeat = false;
    private int previousAVG;

    public Master(MasterCallback masterCallback, int port, int number) throws SocketException {
        super(port, number);
        numbers.add(number);
        this.masterCallback = masterCallback;
        run();
    }

    public void run() throws SocketException {
        datagramSocket = new DatagramSocket(getPort());

        masterCallback.masterMessage("Master mode activated on port " + getPort());

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

    private void broadcast(String string) {
        try {
            InetAddress address = getAddresses(getMyIp(), getSubnetMask());
            datagramSocket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(string.getBytes(), string.length(), address, getPort());
            datagramSocket.send(packet);
            masterCallback.masterMessage("Average number transmitted to: " + address.getCanonicalHostName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getAddresses(String ip, int subnetMask) throws SocketException, UnknownHostException {
        //  Transforming ip to byte massive
        byte[] ipBytes = InetAddress.getByName(ip).getAddress();

        //  Transforming mask number to the number which will be equal to the number of format like it should have
        //  ones after mask length
        int mask = -(1 << (32 - subnetMask));

        //  Transforming ip in type of number that are equal to its byte massive
        int ipAsInt = 0;
        for (byte b : ipBytes) {
            ipAsInt = (ipAsInt << 8) | (b & 0xFF);
        }

        //  Using formula to merge ip and mask
        int broadcastAsInt = ipAsInt | ~mask;
        byte[] broadcastBytes = new byte[4];
        for (int i = 3; i >= 0; i--) {
            broadcastBytes[i] = (byte) (broadcastAsInt & 0xFF);
            broadcastAsInt >>= 8;
        }
        return InetAddress.getByAddress(broadcastBytes);
    }

    private static String getMyIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                //  Filtering inactive and 127.0.0.1 type interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                //  Checking all inet addresses
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    //  Filtering IPv4 addresses
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

    private int getSubnetMask() {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            //  Taking all network masks
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                //  Filtering subnet mask of IPv4 type
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