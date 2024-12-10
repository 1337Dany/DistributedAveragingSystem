package data;

/**
 * The Client class represents a client with a specific port and number.
 */
public class Client {
    private final int port;
    private final int number;

    /**
     * Constructs a new Client with the specified port and number.
     *
     * @param port the port number of the client
     * @param number the unique number of the client
     */
    public Client(int port, int number){
        this.port = port;
        this.number = number;
    }

    /**
     * Returns the port number of the client.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the unique number of the client.
     *
     * @return the client number
     */
    public int getNumber() {
        return number;
    }
}