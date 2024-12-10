import domain.DASPresenter;
import ui.View;

/**
 * The DAS class is the entry point of the application.
 */
public class DAS {

    /**
     * Constructs a DAS object.
     *
     * @param port the port number to bind the socket
     * @param number the initial number to add to the list
     */
    public DAS(int port, int number) {
        View view = new View();
        try {
            new DASPresenter(view, port, number);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            view.printErrorMessage("Incorrect input");
        }
    }

    /**
     * The main method to start the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new DAS(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}