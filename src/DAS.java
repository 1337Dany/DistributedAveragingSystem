import domain.DASPresenter;
import ui.View;

public class DAS {

    public DAS(int port, int number) {
        View view = new View();
        new DASPresenter(view,port, number);
    }

    public static void main(String[] args) {
        try {
            new DAS(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("Incorrect input");
        }
    }
}