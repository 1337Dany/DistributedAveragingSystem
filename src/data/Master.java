package data;

import domain.MasterCallback;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Master extends Client {
    private final MasterCallback masterCallback;
    private DatagramSocket datagramSocket;
    private final List<Integer> numbers = Collections.synchronizedList(new ArrayList<>());
    private static final byte[] BYTE_BUFFER = new byte[1024];

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
                if (packet.getAddress().equals(InetAddress.getLocalHost())) {
                   continue;
                }
                if (received == 0) {
                    int average = avg();
                    masterCallback.masterMessage("Broadcast average number: " + average);
                    masterCallback.masterMessage(String.valueOf(numbers.size()));
                    for (int i = 0; i < numbers.size(); i++) {
                        masterCallback.masterMessage(String.valueOf(numbers.get(i)));
                    }
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
        return (int)Math.round((double) sum / numbers.size());
    }

    private void broadcast(String string){
        try{
            List<InetAddress> listOfAddresses = getAddresses();
            datagramSocket.setBroadcast(true);
            for (InetAddress address : listOfAddresses) {
                DatagramPacket packet = new DatagramPacket(string.getBytes(), string.length(), address, getPort());
                datagramSocket.send(packet);
                masterCallback.masterMessage("Average number transmitted to: " + address.getCanonicalHostName());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<InetAddress> getAddresses() throws SocketException {
        List<InetAddress> result =
                Collections.list(NetworkInterface.getNetworkInterfaces())
                .stream()
                .filter(n -> {
                    try {
                        return ((!n.isLoopback() && n.isUp()));
                    } catch (SocketException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(networkInterface ->
                        networkInterface.getInterfaceAddresses()
                                .stream()
                                .map(InterfaceAddress::getBroadcast)
                                .filter(Objects::nonNull))
                .collect(Collectors.toList());

        return result;
    }


}
