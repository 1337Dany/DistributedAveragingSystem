package domain;

import data.Client;
import data.Master;
import data.Slave;
import ui.ConsoleView;

import java.net.SocketException;

public class DASPresenter implements MasterCallback, SlaveCallback {
    private final ConsoleView consoleView;
    private Client client;

    public DASPresenter(ConsoleView consoleView, int port, int number) {
        this.consoleView = consoleView;
        configure(port, number);
    }

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
    public void slaveMessage(String string) {
        consoleView.printMessage(string);
    }

    @Override
    public void slveErrorMessage(String string) {
        consoleView.printErrorMessage(string);
    }
}
