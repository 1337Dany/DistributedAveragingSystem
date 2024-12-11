package domain;

import data.Client;
import data.Master;
import data.Slave;
import ui.ConsoleView;

import java.net.SocketException;

/**
 * The DASPresenter class handles the interaction between the view and the model.
 */
public class DASPresenter implements MasterCallback, SlaveCallback {
    private final ConsoleView consoleView;
    private Client client;

    /**
     * Constructs a DASPresenter object.
     *
     * @param consoleView the view to display messages
     * @param port the port number to bind the socket
     * @param number the initial number to add to the list
     */
    public DASPresenter(ConsoleView consoleView, int port, int number) {
        this.consoleView = consoleView;
        configure(port, number);
    }

    /**
     * Configures the client as either a master or a slave.
     *
     * @param port the port number to bind the socket
     * @param number the initial number to add to the list
     */
    private void configure(int port, int number) {
        try {
            client = new Master(this, port, number);
        } catch (SocketException e) {
            client = new Slave(this, port, number);
        }
    }

    @Override
    public void masterMessage(String string) {
        consoleView.printMessage(string);
    }

    @Override
    public void masterErrorMessage(String string) {
        consoleView.printErrorMessage(string);
    }

    @Override
    public void slaveMessage(String string) {
        consoleView.printMessage(string);
    }

    @Override
    public void slveErrorMessage(String string) {
        consoleView.printErrorMessage(string);
    }
}