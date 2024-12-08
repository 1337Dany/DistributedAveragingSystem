package data;

public class Client {
    private final int port;
    private final int number;

    public Client(int port, int number){
        this.port = port;
        this.number = number;
    }

    public int getPort() {
        return port;
    }

    public int getNumber() {
        return number;
    }
}
